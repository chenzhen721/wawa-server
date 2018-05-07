package com.wawa.socket;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.wawa.common.doc.Result;
import com.wawa.model.Connection;
import com.wawa.model.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * 用于操作机器指令
 */
public class DollSocketServer extends TextWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(DollSocketServer.class);
    private static Map<String, Connection<DBObject>> players = new ConcurrentHashMap<>();
    private EventBus eventBus = new AsyncEventBus("default_doll", Executors.newCachedThreadPool());
    private Timer timer = new Timer();
    private TimerTask pingTimerTask;

    @Resource
    public MongoTemplate logMongo;

    DBCollection record_log() {
        return logMongo.getCollection("record_log");
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
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("client doll socket建立连接，开始初始化!" + session);
        try {
            Map<String, Object> attr = session.getAttributes();
            if (attr == null || !attr.containsKey("logInfo")) {
                WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
                session.close(CloseStatus.BAD_DATA);
                return;
            }
            //同一个record只能同时有一个客户端连接
            DBObject logInfo = (DBObject) attr.get("logInfo");
            logInfo.put("status", 0); //初始化
            Connection<DBObject> connection = new Connection<>(session.getId(), session, logInfo);
            if (!players.isEmpty()) {
                Iterator<String> iter = players.keySet().iterator();
                while (iter.hasNext()) {
                    String key = iter.next();
                    Connection<DBObject> conn = players.get(key);
                    if (conn != null && conn.getData() != null) {
                        DBObject obj = conn.getData();
                        if (obj.get("_id") != null && obj.get("_id").equals(logInfo.get("_id"))) {
                            players.remove(key);
                            if (conn.getSession() != null && conn.getSession().isOpen()) {
                                WebSocketHelper.send(conn.getSession(), Result.丢失必需参数.toJsonString());
                            }
                            break;
                        }
                    }
                }
            }
            players.put(session.getId(), connection);
            return;
        } catch (Exception e) {
            logger.error("error to establish connection." + session, e);
        }
        WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
        session.close(CloseStatus.BAD_DATA);
    }

    /**
     * onmessage
     * 收到数据,json数据结果  action: operate  data: u d l r doll
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info("" + session + ": " + message);
        Connection<DBObject> conn = players.get(session.getId());
        if (conn == null || conn.getData() == null) {
            return;
        }
        DBObject playerinfo = conn.getData();
        if (!playerinfo.containsField("device_id") || !playerinfo.containsField("status")) {
            logger.debug("cannot find playerinfo on session:" + session + ", message:" + message.getPayload());
            return;
        }
        eventBus.post(new MessageEvent(session, message, playerinfo));
    }

    /**
     * onclose
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("${conn} has left the room!");
        //去掉对应的conn
        if (players.containsKey(session.getId())) {
            players.remove(session.getId());
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
        if (session == null) {
            // some errors like port binding failed may not be assignable to a specific websocket
            return;
        }
        //去掉对应的conn
        if (players.containsKey(session.getId())) {
            players.remove(session.getId());
        }
    }

    /**
     * 心跳时刻
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        super.handlePongMessage(session, message);
        logger.info(session.getId() + message.getPayloadLength());
    }

    public void register(Object object) {
        eventBus.register(object);
    }

    public void unregister(Object object) {
        eventBus.unregister(object);
    }

    //做心跳 没有连接的用户踢出房间
    public static final PingMessage pingMessage = new PingMessage();
    private void heartBeat() {
        //心跳
        if (pingTimerTask == null) {
            pingTimerTask = new TimerTask() {
                @Override
                public void run() {
                    for(Map.Entry<String, Connection<DBObject>> entry : players.entrySet()) {
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
                        players.remove(key);
                        if (value != null) {
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
