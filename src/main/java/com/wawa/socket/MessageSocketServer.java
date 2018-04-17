package com.wawa.socket;

import com.mongodb.DBObject;
import com.wawa.common.util.JSONUtil;
import com.wawa.model.Connection;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageSocketServer extends TextWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(MessageSocketServer.class);
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static Map<String, Connection<DBObject>> users = new ConcurrentHashMap<>();
    private static Map<String, List<String>> rooms = new HashMap<>();
    private Timer timer = new Timer();
    private TimerTask pingTimerTask;

    public void sendToUser(String userId, String content) {
        Connection<DBObject> conn = users.get(userId);
        if (conn != null && conn.getSession() != null) {
            WebSocketSession session = conn.getSession();
            if (session != null && session.isOpen()) {
                WebSocketHelper.send(session, content);
            }
        }
    }

    public void sendToRoom(String roomId, String content) {
        if (rooms.containsKey(roomId)) {
            List<String> list = rooms.get(roomId);
            for(String userId : list) {
                Connection<DBObject> conn = users.get(userId);
                if (conn != null && conn.getSession() != null) {
                    WebSocketSession session = conn.getSession();
                    if (session != null && session.isOpen()) {
                        WebSocketHelper.send(session, content);
                    }
                }
            }
        }
    }

    public void sendToGlobal(final String content) {
        executor.execute(() -> {
            for (Connection<DBObject> sessions : users.values()) {
                WebSocketSession session = sessions.getSession();
                if (session != null && session.isOpen()) {
                    WebSocketHelper.send(session, content);
                }
            }
        });
    }

    @PostConstruct
    public void init() {
        heartBeat();
    }

    /**
     * onopen
     * @param session
     * @throws Exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("建立连接，开始初始化!" + session);
        try {
            Map<String, Object> attr = session.getAttributes();
            logger.info("attr:" + JSONUtil.beanToJson(attr));
            DBObject user = (DBObject) attr.get("user");
            if (user == null) {
                session.close();
                return;
            }
            String userId = String.valueOf(user.get("_id"));
            String current_room_id = "" + user.get("current_room_id");
            List<String> list = rooms.get(current_room_id);
            if (list == null) {
                list = new ArrayList<>();
                rooms.put((String) user.get("current_room_id"), list);
            }
            list.add(userId);
            Connection<DBObject> connection = new Connection<>(userId, session, user);
            users.put(userId, connection);
        } catch (Exception e) {
            logger.error("connection failed." + session + ", Exception:" + e);
            session.close();
        }
    }

    /**
     * onmessage
     * 收到数据
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            logger.info("" + session + ": " + message.getPayload());
            //todo 判断消息范围, 目前的应用场景就是发送消息
            String msg = message.getPayload();
            if (StringUtils.isBlank(msg)) {
                return;
            }
            Map<String, Object> obj = JSONUtil.jsonToMap(msg);
            if (obj == null || !obj.containsKey("action") || !obj.containsKey("data")) {
                logger.error("obj exception." + obj);
                return;
            }
            String action = "" + obj.get("action");
            // 房间内发送消息
            if ("room.chat.pub".equals(action)) {
                Map<String, Object> attr = session.getAttributes();
                Map<String, Object> user = (Map<String, Object>) attr.get("user");
                String userId = String.valueOf(user.get("_id"));
                String room_id = (String) user.get("current_room_id");
                List<String> userList = rooms.get(room_id);
                Map<String, Object> testMsg = new HashMap<>();
                Map<String, Object> data = new HashMap<>();
                Map<String, Object> userInfo = new HashMap<>();
                testMsg.put("_id", userId + System.currentTimeMillis());
                testMsg.put("action", action);
                testMsg.put("data", data);
                data.put("from", userInfo);
                data.put("content", ((Map) obj.get("data")).get("content"));
                userInfo.put("nick_name", user.get("nick_name"));
                String resp = JSONUtil.beanToJson(testMsg);
//                if (userList != null && userList.remove(userId)) {
                if (userList != null) {
                    logger.info("userList size." + userList.size());
                    for (String id: userList) {
                        Connection<DBObject> conn = users.get(id);
                        WebSocketSession webSocketSession = conn.getSession();
                        WebSocketHelper.send(webSocketSession, resp);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("illigal session:" + session + ", message:" + message.getPayload());
        }
    }

    /**
     * onclose
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    @SuppressWarnings("unchecked")
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info(session + " has left the room! status:" + status);
        //去掉对应的conn
        //去掉对应的session
        Map<String, Object> attr = session.getAttributes();
        if (!attr.isEmpty()) {
            Map<String, Object> user = (Map<String, Object>) attr.remove("user");
            if (user != null && !user.isEmpty()) {
                String userId = String.valueOf(user.get("_id"));
                users.remove(userId);
                List<String> list = rooms.get("" + user.get("current_room_id"));
                list.remove(userId);
            }
        }
    }

    /**
     * onerror
     * @param session
     * @param exception
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("sth wrong happened on server." + exception);
    }

    //做心跳 没有连接的用户踢出房间
    public static final PingMessage pingMessage = new PingMessage();
    private void heartBeat() {
        //心跳
        if (pingTimerTask == null) {
            pingTimerTask = new TimerTask() {
                @Override
                public void run() {
                    for(Map.Entry<String, Connection<DBObject>> entry : users.entrySet()) {
                        String key = entry.getKey();
                        Connection<DBObject> value = entry.getValue();
                        try {
                            if (value != null && value.getSession() != null && value.getSession().isOpen()) {
                                logger.debug("ping socket " + value.getSession().getPrincipal() + " send ping.");
                                value.getSession().sendMessage(pingMessage);
                                continue;
                            }
                        } catch (Exception e) {
                            logger.error("error to ping." + e);
                        }
                        users.remove(key);
                        if (value != null) {
                            if (value.getData() != null) {
                                String current_room_id = "" + value.getData().get("current_room_id");
                                if (rooms.containsKey(current_room_id)) {
                                    List<String> list = rooms.get(current_room_id);
                                    list.remove(current_room_id);
                                }
                            }
                            try {
                                if (value.getSession() != null) {
                                    value.getSession().close(CloseStatus.GOING_AWAY);
                                }
                            } catch (Exception e) {
                                logger.error("error to close session.");
                            }
                        }
                    }
                }
            };
            timer.scheduleAtFixedRate(pingTimerTask, 60000, 60000);
        }
    }

}
