package com.wawa.service

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.WriteConcern
import com.wawa.common.doc.Result
import com.wawa.common.util.JSONUtil
import org.apache.commons.lang.StringUtils
import org.java_websocket.WebSocket
import org.java_websocket.WebSocketImpl
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

import static com.wawa.common.util.WebUtils.$$

@Component
class CoreServerService {
    static final Logger logger = LoggerFactory.getLogger(CoreServerService.class)
    public static ExecutorService executor = Executors.newCachedThreadPool()

    @Resource
    public MongoTemplate logMongo
    @Resource
    public WriteConcern writeConcern
    @Resource
    public MachineServerService machineServerService
    @Value('#{application[\'core.hostname\']}')
    public String hostname
    @Value('#{application[\'core.port\']}')
    public int port

    public CoreServerImpl coreServer

    @PostConstruct
    public void init() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port)
        coreServer = new CoreServerImpl(inetSocketAddress)
        coreServer.start()
    }

    //监听事件
    /*class Task implements Callable<Map>{
        private FutureTask futureTask = new FutureTask(this)
        private Map result

        @Override
        Map call() throws Exception {
            return result
        }

        public void setResult(Map result) {
            this.result = result
        }

        public Map get() {
            futureTask.get(10000l, TimeUnit.MILLISECONDS)
        }

        public void execute() {
            executor.submit(futureTask)
        }

        public void cancel() {
            futureTask.cancel(false)
        }

    }*/

    class CoreServerImpl extends WebSocketServer {
        private static Map<WebSocket, BasicDBObject> players = new HashMap<>()
        //private Map<String, Task> messageListener = new ConcurrentHashMap<>()
        //可以考虑使用eventBus来处理对应的返回值回调问题
        public CoreServerImpl(InetSocketAddress address) {
            super(address)
        }

        DBCollection record_log() {
            logMongo.getCollection('record_log')
        }

        /*public WebSocket getByDeviceId(String log_id) {
            return logs.get(log_id)
        }*/

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            String descriptor = handshake.getResourceDescriptor()
            if (StringUtils.isBlank(descriptor)) {
                conn.send(Result.丢失必需参数.toJsonString())
                conn.close()
                return
            }
            def keypaire = parseDescriptor(descriptor)
            if (keypaire == null) {
                conn.send(Result.丢失必需参数.toJsonString())
                conn.close()
                return
            }
            if (!keypaire.containsKey('log_id')) {
                conn.send(Result.丢失必需参数.toJsonString())
                conn.close()
                return
            }
            def log_id = keypaire.get('log_id')
            def logInfo = record_log().findOne($$(_id: log_id)) as BasicDBObject
            if (logInfo == null) {
                conn.send(Result.丢失必需参数.toJsonString())
                conn.close()
                return
            }
            //todo 每一步都要记录状态
            //todo 这里需要做排他 同一个record只能同时有一个客户端连接
            logInfo['websocket'] = conn
            players.put(conn, logInfo)
        }

        //收到来自客户端的操作请求，
        @Override
        public void onMessage(WebSocket conn, String message) {
            logger.debug("" + conn + ": " + message)
            Map msg = JSONUtil.jsonToMap(message) ?: [:]
            String _id = (String) msg.get('_id')
            //todo 向客户端发消息
            //如果是请求结果，同步游戏结果至注册的callback接口
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            logger.debug("${conn} has left the room!")
            //去掉对应的conn
            if (players.containsKey(conn)) {
                players.remove(conn)
            }
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace()
            if (conn != null) {
                // some errors like port binding failed may not be assignable to a specific websocket
                return
            }
            //去掉对应的conn
            if (players.containsKey(conn)) {
                players.remove(conn)
            }
        }

        @Override
        public void onStart() {
            logger.info("==============>Server started!")
        }

        /**
         * 注册监听事件
         * @param event
         */
        /*public void register(String logId, Task task) {
            if (!messageListener.containsKey(logId)) {
                messageListener.put(logId, task)
                TimerTask timerTask = new TimerTask() {
                    @Override
                    void run() {
                        Task removal = messageListener.remove(logId)
                        if (removal != null) {
                            task.execute()
                        }
                    }
                }
                Timer timer = new Timer()
                timer.schedule(timerTask, 60 * 1000L)
            }
        }*/

        /*public void unregister(String logId) {
            messageListener.remove(logId)?.cancel()
        }*/

        private static Map<String, String> parseDescriptor(String path) {
            if (!path.startsWith('/?')) {
                return null
            }
            path = path.substring(2)
            def params = path.split('=') as String[]
            if (params.length % 2 != 0) {
                return null
            }
            Map<String, String> result = new HashMap<>()
            for(int i = 0; i < params.length; i+=2) {
                String key = params[i]
                String value = params[i+1]
                result.put(key, value)
            }
            return result
        }
    }

}
