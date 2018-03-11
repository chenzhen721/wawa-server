package com.wawa.socket;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.wawa.common.doc.Result;
import com.wawa.common.util.JSONUtil;
import com.wawa.common.util.StringHelper;
import com.wawa.model.ActionTypeEnum;
import com.wawa.model.RoomEventEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoSocketServer extends AbstractWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(VideoSocketServer.class);
    private static EventBus allEventBus = new EventBus();
    //记录所有连进来的用户的基础信息
    private static Map<String, Audience> players = new HashMap<>();
    //sessionid: deviceRoom
    private static Map<String, DeviceStream> streams = new HashMap<>();
    @Resource
    private MongoTemplate adminMongo;
    /*DBCollection machine() {
        return adminMongo.getCollection("machine");
    }*/

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("建立连接，开始初始化!" + session);
        try {
            URI uri = session.getUri();
            String path = uri.getPath();

            String descriptor = uri.getQuery();
            if (StringUtils.isBlank(descriptor)) {
                WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
                return;
            }
            Map<String, String> keypaire = StringHelper.parseUri(descriptor);
            if (keypaire == null) {
                WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
                return;
            }
            if (!keypaire.containsKey("device_id") || !keypaire.containsKey("stream")) {
                WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
                return;
            }
            String deviceId = keypaire.get("device_id");
            String stream = keypaire.get("stream");
            Audience audience = players.get(session.getId());
            if (audience == null) {
                audience = new Audience(deviceId, stream, session);
                players.putIfAbsent(session.getId(), audience);
            }
            allEventBus.register(audience);

            String deviceKey = deviceId + "stream" + stream;
            if (path.endsWith("push")) {
                audience.setOwner(true);
                //创建流房间
                DeviceStream deviceStream = streams.get(deviceKey);
                if (deviceStream == null) {
                    deviceStream = new DeviceStream(stream, deviceId, session);
                    streams.put(deviceKey, deviceStream);
                }
                deviceStream.owner = session;
                deviceStream.start.compareAndSet(false, true);
            }
            if (path.endsWith("pull")) {
                String isStart = keypaire.get("start");
                if ("true".equals(isStart)) {
                    audience.setStart(true);
                } else {
                    audience.setStart(false);
                }
                //根据device_id进入房间
                DeviceStream deviceStream = streams.get(deviceKey);
                //没有这个房间，直接退出socket
                if (deviceStream == null) {
                    session.close();
                    return;
                }
                //
                Boolean isRegiser = deviceStream.register(audience);
                if (!isRegiser) {
                    session.close();
                    return;
                }
                deviceStream.play();
            }

        } catch (Exception e) {

        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        //拉流服务器得到流给订阅此流的用户
        Audience audience = players.get(session.getId());
        if (audience == null) {
            return;
        }
        //logger.info("=============>收到信息");
        if (audience.isOwner()) {
            String deviceKey = audience.getDeviceId() + "stream" + audience.getStream();
            DeviceStream deviceStream = streams.get(deviceKey);
            if (deviceStream == null) {
                logger.error("收到机器的推流信息，但是没有房间信息，需要房间重连");
                //给机器端发送消息重新启动
                Map<String, Object> msg = new HashMap<>();
                msg.put("action", ActionTypeEnum.重启指令.getId());
                msg.put("data", true);
                WebSocketHelper.send(session, JSONUtil.beanToJson(msg));
                return;
            }
            RoomEvent roomEvent = new RoomEvent(message.getPayload(), RoomEventEnum.STREAM); //推流
            deviceStream.post(roomEvent);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //拉流服务器得到流给订阅此流的用户
        Audience audience = players.get(session.getId());
        if (audience == null) {
            return;
        }
        if (!audience.isOwner()) {
            if (message.getPayloadLength() > 0) {
                String msg = message.getPayload();
                //todo 支持切换流

            }
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        logger.debug("session: " + session + " ,message" + message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        //目前想不出场景，需要做处理
        logger.error("session:" + session + " error:" + exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("" + session + ": " + status);
        //获取session信息
        try {
            Audience audience = players.remove(session.getId());
            if (audience == null) {
                return;
            }
            allEventBus.unregister(audience);
            String deviceKey = audience.getDeviceId() + "stream" + audience.getStream();
            DeviceStream deviceStream = streams.get(deviceKey);
            if (deviceStream != null) {
                //踢出房间
                deviceStream.kickoff(session);
            }
        } catch (Exception e) {
            logger.error("" + e);
        }
    }

    /**
     * 单个视频流对象
     */
    public class DeviceStream {
        private EventBus roomEventBus = new EventBus();
        private String stream;  //流名称
        private String deviceId;  //设备ID
        private WebSocketSession owner;
        private Map<WebSocketSession, Audience> allAudience = new HashMap<>();
        private AtomicBoolean start = new AtomicBoolean(true);

        public  DeviceStream(String stream, String deviceId, WebSocketSession owner) {
            this.stream = stream;
            this.deviceId = deviceId;
            this.owner = owner;
            this.roomEventBus.register(this);
        }

        //判断是否开始推流
        public boolean register(Audience audience) {
            roomEventBus.register(audience);
            allAudience.put(audience.getWebSocketSession(), audience);
            return true;
        }

        public void unregister(Audience audience) {
            roomEventBus.unregister(audience);
        }

        public void post(RoomEvent roomEvent) {
            roomEventBus.post(roomEvent);
        }

        @Subscribe
        public void DeadMessage(DeadEvent deadEvent) {
            if (deadEvent.getEvent()!= null) {
                if (deadEvent.getEvent() instanceof RoomEvent) {
                    RoomEvent event = (RoomEvent) deadEvent.getEvent();
                    if (event.getType() == RoomEventEnum.STREAM) {
                        off();
                    }
                }
            }
        }

        //踢出房间
        public void kickoff(WebSocketSession session) {
            if (session.equals(owner)) {
                //交给统一的心跳完成???直播掉线
                return;
            }
            Audience audience = allAudience.remove(session);
            if (audience != null) {
                unregister(audience);
            }
        }

        //发送流信息
        public boolean play() {
            if (owner == null) {//todo isOpen总是返回false
                return false;
            }
            if (this.start.compareAndSet(false, true)) {
                Map<String, Object> msg = new HashMap<>();
                msg.put("action", ActionTypeEnum.推流指令.getId());
                msg.put("data", true);
                WebSocketHelper.send(owner, JSONUtil.beanToJson(msg));
                return true;
            }
            return false;
        }

        //关闭流信息
        public Boolean off() {
            if (owner == null) { //todo isOpen总是返回false
                return false;
            }
            if (this.start.compareAndSet(true, false)) {
                logger.info("no audience registered, off stream.");
                Map<String, Object> msg = new HashMap<>();
                msg.put("action", ActionTypeEnum.推流指令.getId());
                msg.put("data", false);
                WebSocketHelper.send(owner, JSONUtil.beanToJson(msg));
                return true;
            }
            return false;
        }

    }

    public class Audience {
        private String deviceId; //所在房间
        private String stream; //当前关注的视频流
        private WebSocketSession webSocketSession; //对应的session
        private boolean isOwner;//是否主播
        private boolean start; //是否开始推流


        public Audience(String deviceId, String stream, WebSocketSession webSocketSession) {
            this.deviceId = deviceId;
            this.stream = stream;
            this.webSocketSession = webSocketSession;
        }

        /**
         * 房间推送的消息在这里接收
         * @param event
         */
        @Subscribe
        public void handle(RoomEvent event) {
            try {
                if (webSocketSession.isOpen() && RoomEventEnum.STREAM == event.getType() && start) {
                    BinaryMessage binaryMessage = new BinaryMessage(event.getBinary());
                    webSocketSession.sendMessage(binaryMessage);
                }
            } catch (IOException e) {
                logger.info("failed to send stream message to:" + webSocketSession + ": Exception: " + e);
            }
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getStream() {
            return stream;
        }

        public void setStream(String stream) {
            this.stream = stream;
        }

        public WebSocketSession getWebSocketSession() {
            return webSocketSession;
        }

        public void setWebSocketSession(WebSocketSession webSocketSession) {
            this.webSocketSession = webSocketSession;
        }

        public boolean isOwner() {
            return isOwner;
        }

        public void setOwner(boolean owner) {
            isOwner = owner;
        }

        public boolean isStart() {
            return start;
        }

        public void setStart(boolean start) {
            this.start = start;
        }
    }

    /**
     * 房间内消息通信
     */
    public class RoomEvent {
        private ByteBuffer binary;
        private RoomEventEnum type;

        RoomEvent(ByteBuffer binary, RoomEventEnum type) {
            this.binary = binary;
            this.type = type;
        }

        ByteBuffer getBinary() {
            return binary;
        }

        public void setBinary(ByteBuffer binary) {
            this.binary = binary;
        }

        public RoomEventEnum getType() {
            return type;
        }

        public void setType(RoomEventEnum type) {
            this.type = type;
        }
    }

    public static void main(String[] args) {

    }



}
