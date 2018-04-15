package com.wawa.socket;

import com.wawa.common.util.JSONUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageSocketServer extends TextWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(MessageSocketServer.class);
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static Map<String, Map<String, Object>> users = new HashMap<>();
    private static Map<String, List<String>> rooms = new HashMap<>();

    public void sendToRoom(String roomId) {
        if (rooms.containsKey(roomId)) {
            rooms.get(roomId);
        }
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
            Map<String, Object> user = (Map<String, Object>) attr.get("user");
            user.put("socket_session", session);
            String userId = String.valueOf(user.get("_id"));
            users.put(userId, user);
            List<String> list = rooms.get((String) user.get("current_room_id"));
            if (list == null) {
                list = new ArrayList<>();
                rooms.put((String) user.get("current_room_id"), list);
            }
            list.add(userId);

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
//                if (userList != null && userList.remove(userId)) {
                logger.info("userList size." + userList.size());
                if (userList != null) {
                    for (String id: userList) {
                        Map<String, Object> u = users.get(id);
                        WebSocketSession webSocketSession = (WebSocketSession) u.get("socket_session");
                        Map<String, Object> testMsg = new HashMap<>();
                        Map<String, Object> data = new HashMap<>();
                        Map<String, Object> userInfo = new HashMap<>();
                        testMsg.put("_id", userId + System.currentTimeMillis());
                        testMsg.put("action", action);
                        testMsg.put("data", data);
                        data.put("from", userInfo);
                        data.put("content", ((Map) obj.get("data")).get("content"));
                        userInfo.put("nick_name", user.get("nick_name"));
                        WebSocketHelper.send(webSocketSession, JSONUtil.beanToJson(testMsg));
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
        //todo 去掉对应的session
        Map<String, Object> attr = session.getAttributes();
        if (!attr.isEmpty()) {
            Map<String, Object> user = (Map<String, Object>) attr.get("user");
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
        /*if (session == null) {
            return;
        }
        //todo 去掉对应的session
        Map<String, Object> attr = session.getAttributes();
        Map<String, Object> user = (Map<String, Object>) attr.get("user");
        String userId = (String) user.get("_id");
        users.remove(userId);
        List<String> list = rooms.get("" + user.get("current_room_id"));
        list.remove(userId);*/
    }

    //todo 做心跳 没有连接的用户踢出房间

}
