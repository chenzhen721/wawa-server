package com.wawa.socket;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.wawa.api.event.Task;
import com.wawa.common.doc.Result;
import com.wawa.common.util.JSONUtil;
import com.wawa.common.util.StringHelper;
import com.wawa.model.ActionResult;
import com.wawa.model.ActionTypeEnum;
import com.wawa.model.Response;
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

public class MachineSocketServer extends TextWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(MachineSocketServer.class);
    @Resource
    private MongoTemplate adminMongo;
    @Resource
    private WriteConcern writeConcern;
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static Map<String, DBObject> machines = new HashMap<>();
    private static Map<String, WebSocketSession> devices = new HashMap<>();
    private final Map<String, Task> messageListener = new ConcurrentHashMap<>();
    public static final TypeFactory typeFactory = TypeFactory.defaultInstance();

    DBCollection machine() {
        return adminMongo.getCollection("machine");
    }

    public WebSocketSession getByDeviceId(String device_id) {
        return devices.get(device_id);
    }

    public DBObject remote(String device_id) {
        WebSocketSession session = devices.remove(device_id);
        return machines.remove(session.getId());
    }

    /**
     * onopen
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("建立连接，开始初始化!" + session);
        try {
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
        } catch (Exception e) {
            logger.error("connection failed." + session);
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
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            logger.info("" + session + ": " + message.getPayload());
            if (StringUtils.isBlank(message.getPayload())) {
                return;
            }
            JavaType javaType = typeFactory.constructParametricType(Response.class, ActionResult.class);
            Response<ActionResult> msg = JSONUtil.jsonToBean(message.getPayload(), javaType);

            String _id = msg.getId();
            if (_id != null) {
                Task task = messageListener.remove(_id);
                if (task != null) {
                    task.setResult(msg);
                    task.execute(executor);
                }
            }

            //todo 游戏结果callback log_id record_id
            if (msg.getCode() == 1) {
                ActionResult actionResult = msg.getData();
                if (ActionTypeEnum.操控指令.getId().equals(actionResult.getAction_type())
                        && StringUtils.isNotBlank(actionResult.getResult())) {

                }
            }



            //WebSocketHelper.send(session, "received msg.");
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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info(session + " has left the room! status:" + status);
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
        logger.error("sth wrong happened on server." + exception);
        if (session == null) {
            return;
        }
        //去掉对应的session
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
