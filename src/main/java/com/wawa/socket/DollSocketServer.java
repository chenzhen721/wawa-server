package com.wawa.socket;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.wawa.api.event.Task;
import com.wawa.common.doc.Result;
import com.wawa.common.util.JSONUtil;
import com.wawa.common.util.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wawa.common.doc.MongoKey._id;
import static com.wawa.common.util.WebUtils.$$;

public class DollSocketServer extends TextWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(DollSocketServer.class);
    @Resource
    private MongoTemplate logMongo;
    @Resource
    private WriteConcern writeConcern;
    private static Map<String, DBObject> players = new HashMap<>();

    DBCollection record_log() {
        return logMongo.getCollection("record_log");
    }

    /**
     * onopen
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("建立连接，开始初始化!" + session);
        URI uri = session.getUri();
        String descriptor = uri.getQuery();
        //todo 这些操作都可以放到interceptor中完成
        if (StringUtils.isBlank(descriptor)) {
            WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
            session.close();
            return;
        }
        Map<String, String> keypaire = StringHelper.parseUri(descriptor);
        if (keypaire == null) {
            WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
            session.close();
            return;
        }
        if (!keypaire.containsKey("log_id")) {
            WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
            session.close();
            return;
        }
        String log_id = keypaire.get("log_id");
        DBObject logInfo = record_log().findOne($$(_id, log_id));
        if (logInfo == null) {
            WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
            session.close();
            return;
        }
        //todo 每一步都要记录状态
        //todo 这里需要做排他 同一个record只能同时有一个客户端连接
        logInfo.put("player", session);
        players.put(session.getId(), logInfo);
    }

    /**
     * onmessage
     * 收到数据
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.debug("" + session + ": " + message);
        Map msg = JSONUtil.jsonToMap(message.getPayload());
        if (msg == null) {
            return;
        }
        String _id = (String) msg.get("_id");
        //todo 向客户端发消息
        //如果是请求结果，同步游戏结果至注册的callback接口
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
        logger.debug(session.getId() + message.getPayloadLength());
    }

}
