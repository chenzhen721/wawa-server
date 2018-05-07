package com.wawa.socket;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.wawa.common.doc.Result;
import com.wawa.common.util.JSONUtil;
import com.wawa.common.util.StringHelper;
import com.wawa.model.ActionTypeEnum;
import com.wawa.model.RoomEventEnum;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoSocketServer extends AbstractWebSocketHandler {
    private static Logger logger = LoggerFactory.getLogger(VideoSocketServer.class);
    private static EventBus allEventBus = new AsyncEventBus("default_all", Executors.newCachedThreadPool());
    //记录所有连进来的用户的基础信息
    private static Map<String, Audience> players = new HashMap<>();
    //sessionid: deviceRoom
    private static Map<String, DeviceStream> streams = new HashMap<>();
    private Map<String, Record> recordMap = new HashMap<>();
    @Resource
    private MongoTemplate adminMongo;
    /*DBCollection machine() {
        return adminMongo.getCollection("machine");
    }*/

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("video stream 建立连接，开始初始化!" + session);
        try {
            Map<String, Object> attr = session.getAttributes();
            if (attr == null || !attr.containsKey("deviceId") || !attr.containsKey("stream") || !attr.containsKey("isPush")) {
                WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
                session.close(CloseStatus.BAD_DATA);
                return;
            }
            String deviceId = "" + attr.get("deviceId");
            String stream = "" + attr.get("stream");
            Boolean isPush = Boolean.parseBoolean("" + attr.get("isPush"));

            Audience audience = players.get(session.getId());
            if (audience == null) {
                audience = new Audience(deviceId, stream, session);
                players.putIfAbsent(session.getId(), audience);
            }
            allEventBus.register(audience);

            String deviceKey = deviceId + "stream" + stream;
            //创建流房间
            DeviceStream deviceStream = streams.get(deviceKey);
            if (deviceStream == null) {
                deviceStream = new DeviceStream(stream, deviceId, null);
                streams.put(deviceKey, deviceStream);
            }
            if (isPush) {
                audience.setOwner(true);
                deviceStream.owner = session;
                deviceStream.start.compareAndSet(false, true);
            } else {
                audience.setStart(true);
                //根据device_id进入房间
                deviceStream.register(audience);
                deviceStream.play();
            }
            return;
        } catch (Exception e) {
            logger.error("error to establish connection." + session, e);
        }
        WebSocketHelper.send(session, Result.丢失必需参数.toJsonString());
        session.close(CloseStatus.BAD_DATA);
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
            //todo 这个地方看看有没有优化空间
            RoomEvent roomEvent = new RoomEvent(message, null, message.getPayloadLength(), RoomEventEnum.STREAM); //推流
            deviceStream.post(roomEvent);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        logger.info(session + ", message:" + message.getPayload());
        //拉流服务器得到流给订阅此流的用户
        Audience audience = players.get(session.getId());
        if (audience == null) {
            return;
        }
        if (!audience.isOwner()) {
            if (message.getPayloadLength() > 0) {
                String deviceKey = audience.getDeviceId() + "stream" + audience.getStream();
                DeviceStream deviceStream = streams.get(deviceKey);
                String msg = message.getPayload();
                MessageEvent roomEvent = new MessageEvent(msg, RoomEventEnum.TOGGLE); //推流
                deviceStream.post(roomEvent);
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
            logger.error("", e);
        }
    }

    public boolean record_open(String logId, String deviceId) {
        Record record = recordMap.get(logId);
        if (record != null) {
            return true;
        }
        record = new Record(logId, deviceId);
        logger.info("init record instance. result:" + record.newInstance());
        recordMap.put(logId, record);
        String deviceKey = deviceId + "stream" + record.getStream();

        //创建流房间
        DeviceStream deviceStream = streams.get(deviceKey);
        if (deviceStream == null) {
            deviceStream = new DeviceStream(record.getStream(), deviceId, null);
            streams.put(deviceKey, deviceStream);
        }
        //根据device_id进入房间
        deviceStream.register(record);
        deviceStream.play();
        return true;
    }

    public boolean record_off(String logId, String deviceId) {
        logger.info("record map:" + recordMap);
        Record record = recordMap.remove(logId);
        logger.info("off record:" + logId + " record:" + record);
        if (record != null) {
            record.stop();
        }
        return true;
    }

    /**
     * 单个视频流对象
     */
    public class DeviceStream {
        private EventBus roomEventBus = new AsyncEventBus("default_room", Executors.newCachedThreadPool());
        private Map<String, Audience> records = new HashMap<>(); //开启记录
        private String stream;  //流名称
        private String deviceId;  //设备ID
        private WebSocketSession owner;
        private Map<WebSocketSession, Audience> allAudience = new HashMap<>();
        private AtomicBoolean start = new AtomicBoolean(true);
        private byte[] SPSPPS;

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

        public boolean register(Record record) {
            roomEventBus.register(record);
            return true;
        }

        public void unregister(Record record) {
            roomEventBus.unregister(record);
        }

        public void post(Object roomEvent) {
            roomEventBus.post(roomEvent);
        }

        @Subscribe
        public void DeadMessage(DeadEvent deadEvent) {
            if (deadEvent.getEvent()!= null) {
                if (deadEvent.getEvent() instanceof RoomEvent) {
                    RoomEvent event = (RoomEvent) deadEvent.getEvent();
                    if (event.getType() == RoomEventEnum.STREAM && records.isEmpty()) {
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
            if (owner == null) {//isOpen总是返回false
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
            if (owner == null) { //isOpen总是返回false
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

        public byte[] getSPSPPS() {
            return SPSPPS;
        }

        public void setSPSPPS(byte[] SPSPPS) {
            this.SPSPPS = SPSPPS;
        }
    }

    private File pic_folder;

    public static final DateFormat df = new SimpleDateFormat("yyyyMMdd");

    @Value("#{application['pic.folder']}")
    void setPicFolder(String folder) {
        pic_folder = new File(folder);
        if (!pic_folder.exists()) {
            System.out.println(pic_folder.mkdirs());
        }
        System.out.println("初始化图片上传目录 : ${folder}");
    }

    // 新建record类 用于保存用户的操作记录 带上时间计算， 到时间停止并且自我销毁
    public class Record {
        private Executor exec = Executors.newFixedThreadPool(1);
        private String logId; //记录ID
        private String deviceId; //所在房间
        private String stream = "1"; //默认录制视频流1
        private FileOutputStream outputStream;
        private File target;
        private Timer timer = new Timer();

        public Record(String logId, String deviceId) {
            this.logId = logId;
            this.deviceId = deviceId;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    stop();
                }
            }, 60 * 1000);
        }

        public boolean newInstance() {
            String filePath = df.format(new Date());
            try {
                File folder = new File(pic_folder, filePath);
                this.target = new File(folder,"/" + logId + ".h264");
                if (!folder.exists()) {
                    logger.info("mkdirs:" + folder.mkdirs());
                }
                if (!this.target.exists()) {
                    logger.info("createNewFile:" + this.target.createNewFile());
                }
                if (this.target.exists()) {
                    if (outputStream == null) {
                        try {
                            outputStream = new FileOutputStream(this.target, true);
                            return true;
                        } catch (FileNotFoundException e) {
                            logger.error("error to create file" + filePath, e);
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("error to record." + logId, e);
            }
            return false;
        }

        @Subscribe
        public void handle(RoomEvent event) {
            try {
                if (RoomEventEnum.STREAM == event.getType()) {
                    if (event.getMessage() != null && outputStream != null) {
                        exec.execute(() -> {
                            try {
                                BinaryMessage message = event.getMessage();
                                byte[] bytes = message.getPayload().array();
                                if (bytes.length < 5) {
                                    return;
                                }
                                outputStream.write(message.getPayload().array(), 0, message.getPayloadLength());
                            } catch (Exception e) {
                                logger.info("failed to write stream to:" + target.getPath(), e);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                logger.info("failed to write stream, Exception: ", e);
            }
        }

        public boolean stop() {
            exec.execute(() -> {
                try {
                    String deviceKey = deviceId + "stream" + this.stream;
                    DeviceStream deviceStream = streams.get(deviceKey);
                    if (deviceStream != null) {
                        deviceStream.unregister(this);
                    }
                    if (timer != null) {
                        timer.cancel();
                    }
                    if (this.outputStream != null) {
                        outputStream.close();
                    }
                    logger.info("到这儿了么？");
                    if (this.target != null) {
                        String path = this.target.getPath();
                        String newPath = path.replace("h264", "mp4");
                        String exec = "/usr/local/ffmpeg/bin/ffmpeg -i " + path + " -vcodec copy -f mp4 " + newPath;
                        Process process = Runtime.getRuntime().exec(exec);
                        logger.info(exec + ". exit value" + process.exitValue());
                        logger.info("delete original file result." + target.delete());
                    }
                } catch (IOException e) {
                    logger.error("error to close record." + logId, e);
                }
            });
            return true;
        }

        public String getLogId() {
            return logId;
        }

        public void setLogId(String logId) {
            this.logId = logId;
        }

        public String getStream() {
            return stream;
        }

        public void setStream(String stream) {
            this.stream = stream;
        }
    }


    public class Audience {
        private Executor exec = Executors.newFixedThreadPool(1);
        private String deviceId; //所在房间
        private String stream; //当前关注的视频流
        private WebSocketSession webSocketSession; //对应的session
        private boolean isOwner;//是否主播
        private boolean start; //是否开始推流
        private int period = 0;
        private boolean needsps = true;


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
                    exec.execute(() -> {
                        if (needsps) {
                            Map<String, String> msg = new HashMap<>();
                            msg.put("type", "restart");
                            WebSocketHelper.send(webSocketSession, JSONUtil.beanToJson(msg));
                            needsps = false;
                        }
                    });
                    exec.execute(() -> {
                        try {
                            BinaryMessage binaryMessage = event.getMessage();
                            byte[] bytes = binaryMessage.getPayload().array();
                            if (bytes.length < 5) {
                                return;
                            }
                            webSocketSession.sendMessage(new BinaryMessage(bytes, 0, binaryMessage.getPayloadLength(), false));
                        } catch (IOException e) {
                            logger.info("failed to send stream message to:" + webSocketSession + ": Exception: ", e);
                        }
                    });
                }
            } catch (Exception e) {
                logger.info("failed to send stream message to:" + webSocketSession + ": Exception: ", e);
            }
        }

        /**
         * 房间推送的消息在这里接收
         * @param event
         */
        @Subscribe
        public void handle(MessageEvent event) {
            try {
                if (webSocketSession.isOpen() && RoomEventEnum.TOGGLE == event.getType()) {
                    logger.info("toggle...");
                    Map<String, Object> obj = JSONUtil.jsonToMap(event.getMsg());
                    if (!obj.containsKey("videoType")) {
                        return;
                    }
                    String stream = "" + obj.get("videoType");
                    if (!this.stream.equals(stream)) {
                        //第一步删除原来订阅的流的信息
                        String deviceKey = this.getDeviceId() + "stream" + this.getStream();
                        DeviceStream deviceStream = streams.get(deviceKey);
                        if (deviceStream != null) {
                            //踢出房间
                            deviceStream.kickoff(webSocketSession);
                        }
                        //第二步添加订阅至目标流的信息
                        deviceKey = deviceId + "stream" + stream;
                        //创建流房间
                        deviceStream = streams.get(deviceKey);
                        if (deviceStream == null) {
                            deviceStream = new DeviceStream(stream, deviceId, null);
                            streams.put(deviceKey, deviceStream);
                        }
                        this.stream = stream;
                        this.needsps = true;
                        this.setStart(true);
                        //根据device_id进入房间
                        deviceStream.register(this);
                        deviceStream.play();
                        this.needsps = true;
                    }
                }
            } catch (Exception e) {
                logger.info("failed to toggle stream to:" + webSocketSession, e);
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
        private byte[] binary;
        private RoomEventEnum type;
        private int length;
        private BinaryMessage message;

        RoomEvent(BinaryMessage message, byte[] binary, int len, RoomEventEnum type) {
            this.message = message;
            this.binary = binary;
            this.length = len;
            this.type = type;
        }

        byte[] getBinary() {
            return binary;
        }

        public void setBinary(byte[] binary) {
            this.binary = binary;
        }

        public RoomEventEnum getType() {
            return type;
        }

        public void setType(RoomEventEnum type) {
            this.type = type;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public BinaryMessage getMessage() {
            return message;
        }

        public void setMessage(BinaryMessage message) {
            this.message = message;
        }
    }

    /**
     * 房间内消息通信
     */
    public class MessageEvent {
        private String msg;
        private RoomEventEnum type;

        MessageEvent(String msg, RoomEventEnum type) {
            this.msg = msg;
            this.type = type;
        }

        public String getMsg() {
            return msg;
        }

        public RoomEventEnum getType() {
            return type;
        }

    }

    public static void main(String[] args) {

    }



}
