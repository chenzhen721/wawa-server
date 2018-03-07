package com.wawa.socket;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.wawa.common.doc.Result;
import com.wawa.common.util.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wawa.common.doc.MongoKey._id;
import static com.wawa.common.util.WebUtils.$$;

public class VideoSocketServer extends BinaryWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(VideoSocketServer.class);
    private static EventBus allEventBus = new EventBus();
    //记录所有连进来的用户的基础信息
    private static Map<String, Audience> players = new HashMap<>();
    //sessionid: deviceRoom
    private static Map<String, DeviceStream> streams = new HashMap<>();
    //todo 这里需要有个数据结构存储对应的推拉流方
    //key为机器号+流名称  value对应session
    /*Map<String, WebSocketSession> streamMaps = new HashMap<>();*/
    @Resource
    private MongoTemplate adminMongo;
    DBCollection machine() {
        return adminMongo.getCollection("machine");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
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
            audience.setOwner(true);
            allEventBus.register(audience);

            if (path.endsWith("push")) {
                //todo 创建流房间
                streams.putIfAbsent(deviceId + stream, new DeviceStream(stream, deviceId));
                //自己不需要注册订阅事件
            }
            if (path.endsWith("pull")) {
                //todo 根据device_id进入房间
                DeviceStream deviceStream = streams.get(deviceId + stream);
                if (deviceStream == null) {
                    session.close();
                    return;
                }
                deviceStream.register(audience);
            }



        } catch (Exception e) {

        }
        //todo 开启推流

        //todo 定期检查socket连接是否可用

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        //拉流服务器得到流给订阅此流的用户
        Audience audience = players.get(session.getId());
        if (audience == null) {
            return;
        }
        if (audience.isOwner()) {
            String deviceKey = audience.getDeviceId() + audience.getStream();
            DeviceStream deviceStream = streams.get(deviceKey);
            if (deviceStream == null) {
                logger.error("是个主播但是没有流信息"); //todo 需要补充
                return;
            }
            RoomEvent roomEvent = new RoomEvent(message.getPayload(), "stream"); //todo type定义需要改
            deviceStream.post(roomEvent);
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("sth error.");
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
            String deviceKey = audience.getDeviceId() + audience.getStream();
            DeviceStream deviceStream = streams.get(deviceKey);
            if (deviceStream != null) {
                deviceStream.unregister(audience);
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
        //private List<String> players = new ArrayList<>();
        private String stream;
        private String deviceId;
        private WebSocketSession owner;

        public  DeviceStream(String stream, String deviceId) {
            this.stream = stream;
            this.deviceId = deviceId;
            this.owner = owner;
        }

        public void register(Audience audience) {
            roomEventBus.register(audience);
        }

        public void unregister(Audience audience) {
            roomEventBus.unregister(audience);
        }

        public void post(RoomEvent roomEvent) {
            roomEventBus.post(roomEvent);
        }

        @Subscribe
        public void DeadMessage(DeadEvent deadEvent) {
            logger.info("received dead event" + deadEvent);
        }

    }

    public class Audience {
        private String deviceId; //所在房间
        private String stream; //当前关注的视频流
        private WebSocketSession webSocketSession; //对应的session
        private boolean isOwner;//是否主播


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
                if ("stream".equals(event.getType())) {
                    BinaryMessage binaryMessage = new BinaryMessage(event.getBinary());
                    webSocketSession.sendMessage(binaryMessage);
                }
            } catch (IOException e) {
                logger.error("failed to sead stream message to:" + webSocketSession);
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
    }

    public class RoomEvent {
        private ByteBuffer binary;
        private String type; //todo

        public RoomEvent(ByteBuffer binary, String type) {
            this.binary = binary;
            this.type = type;
        }

        public ByteBuffer getBinary() {
            return binary;
        }

        public void setBinary(ByteBuffer binary) {
            this.binary = binary;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static void main(String[] args) {

    }



}
