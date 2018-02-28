package com.wawa.socket;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.wawa.api.Web;
import com.wawa.api.event.Task;
import com.wawa.common.doc.Result;
import com.wawa.common.util.JSONUtil;
import com.wawa.common.util.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
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

public class WebsocketEndPoint extends TextWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(WebsocketEndPoint.class);
    @Resource
    private MongoTemplate adminMongo;
    @Resource
    private WriteConcern writeConcern;
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static Map<String, DBObject> machines = new HashMap<>();
    private static Map<String, WebSocketSession> devices = new HashMap<>();
    private final Map<String, Task> messageListener = new ConcurrentHashMap<>();

    DBCollection machine() {
        return adminMongo.getCollection("machine");
    }

    public WebSocketSession getByDeviceId(String device_id) {
        return devices.get(device_id);
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
            return;
        }
        Map<String, String> keypaire = StringHelper.parseUri(descriptor);
        if (keypaire == null) {
            WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
            return;
        }
        if (!keypaire.containsKey("device_id")) {
            WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
            return;
        }
        String device_id = keypaire.get("device_id");
        DBObject deviceInfo = machine().findOne($$(_id, device_id));
        deviceInfo.put("websocket", session);
        machines.put(session.getId(), deviceInfo);
        devices.put(device_id, session);
        WebSocketHelper.send(session, "opening success!");
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
        if (StringUtils.isBlank(message.getPayload())) {
            return;
        }
        Map msg = JSONUtil.jsonToMap(message.getPayload());

        String _id = (String) msg.get("_id");
        Task task = messageListener.remove(_id);
        if (task != null) {
            task.execute(executor);
        }
        WebSocketHelper.send(session, "received msg.");
    }

    /**
     * onclose
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.debug("${conn} has left the room!");
        //去掉对应的conn
        if (machines.containsKey(session.getId())) {
            DBObject machineinfo = machines.remove(session.getId());
            devices.remove(String.valueOf(machineinfo.get("device_id")));
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
        if (machines.containsKey(session.getId())) {
            DBObject machineinfo = machines.remove(session.getId());
            devices.remove(String.valueOf(machineinfo.get("device_id")));
        }
    }

    /**
     * 注册监听事件
     */
    public void register(final String logId, final Task task) {
        if (!messageListener.containsKey(logId)) {
            messageListener.put(logId, task);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Task removal = messageListener.remove(logId);
                    if (removal != null) {
                        task.execute(executor);
                    }
                }
            }, 60 * 1000L);
        }
    }

    public void unregister(String logId) {
        Task task = messageListener.remove(logId);
        if (task != null) {
            task.cancel();
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
