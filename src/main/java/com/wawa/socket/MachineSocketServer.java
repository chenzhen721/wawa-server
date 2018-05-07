package com.wawa.socket;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.wawa.api.event.Task;
import com.wawa.common.doc.Result;
import com.wawa.common.util.HttpClientUtils;
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
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wawa.common.doc.MongoKey.$set;
import static com.wawa.common.doc.MongoKey._id;
import static com.wawa.common.util.WebUtils.$$;

public class MachineSocketServer extends TextWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(MachineSocketServer.class);
    @Resource
    private MongoTemplate adminMongo;
    @Resource
    private MongoTemplate logMongo;
    @Resource
    private WriteConcern writeConcern;
    @Resource
    private VideoSocketServer videoSocketServer;
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static Map<String, DBObject> machines = new HashMap<>();
    private static Map<String, WebSocketSession> devices = new HashMap<>();
    private final Map<String, Task> messageListener = new ConcurrentHashMap<>();
    public static final TypeFactory typeFactory = TypeFactory.defaultInstance();

    DBCollection machine() {
        return adminMongo.getCollection("machine");
    }
    DBCollection record_log() {
        return logMongo.getCollection("record_log");
    }

    public WebSocketSession getByDeviceId(String device_id) {
        return devices.get(device_id);
    }

    public DBObject remove(String device_id) {
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
        logger.info("machine socket建立连接，开始初始化!" + session);
        Map<String, Object> attr = session.getAttributes();
        if (attr == null || !attr.containsKey("deviceInfo")) {
            WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        try {
            DBObject deviceInfo = (DBObject) attr.get("deviceInfo");
            deviceInfo.put("websocket", session);
            machines.put(session.getId(), deviceInfo);
            devices.put("" + deviceInfo.get("_id"), session);
            return;
        } catch (Exception e) {
            logger.error("connection failed." + session);
        }
        WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
        session.close(CloseStatus.BAD_DATA);
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

            //游戏结果callback
            if (msg.getCode() == 1) {
                ActionResult actionResult = msg.getData();
                if (ActionTypeEnum.操控指令.getId().equals(actionResult.getAction_type())
                        && StringUtils.isNotBlank(actionResult.getResult())) {
                    //游戏结果回调
                    String logId = actionResult.getLog_id();
                    //收到游戏结果 更新record_log
                    BasicDBObject query = $$("_id", logId).append("status", 0);
                    BasicDBObject update = $$("status", 1);
                    DBObject deviceInfo = machines.get(session.getId());
                    if (deviceInfo != null) {
                        String callback_url = "" + deviceInfo.get("callback_url");
                        DBObject record = record_log().findOne(logId);
                        if (StringUtils.isBlank(callback_url) || record == null) {
                            logger.error("callback_url or record missing." + logId);
                        } else {
                            Map<String, String> params = new HashMap<>();
                            params.put("log_id", actionResult.getLog_id());
                            params.put("record_id", "" + record.get("record_id"));
                            params.put("operate_result", actionResult.getResult());
                            params.put("sign", "");
                            params.put("ts", "");
                            try {
                                String resp = HttpClientUtils.post(callback_url, params, null);
                                if (resp != null) {
                                    Response response = JSONUtil.jsonToBean(resp, Response.class);
                                    if (response != null && response.getCode() == 1) {
                                        update.append("updated", true);
                                    }
                                }
                            } catch (Exception e) {
                                logger.error("error to get callback_url:" + callback_url + " ,with params" + JSONUtil.beanToJson(params));
                            }
                            //收到结束回调
                            logger.info("结束回调");
                            videoSocketServer.record_off("" + record.get("record_id"), "" + deviceInfo.get("_id"));
                        }
                    }
                    update.append("operate_result", Boolean.parseBoolean(actionResult.getResult()));
                    if (1 != record_log().update(query, $$($set, update), false, false, writeConcern).getN()) {
                        logger.error("failed to update record_log: " + logId + " ,update:" + update);
                    }
                }
            }

        } catch (Exception e) {
            logger.error("illigal session:" + session + ", message:" + message.getPayload(), e);
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
