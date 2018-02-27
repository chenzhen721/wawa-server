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
class MachineServerService {
    static final Logger logger = LoggerFactory.getLogger(MachineServerService.class)
    public static ExecutorService executor = Executors.newCachedThreadPool()

    @Resource
    public MongoTemplate adminMongo
    @Resource
    public WriteConcern writeConcern
    @Value('#{application[\'machine.hostname\']}')
    public String hostname
    @Value('#{application[\'machine.port\']}')
    public int port

    public MachineServerImpl machineServer

    @PostConstruct
    public void init() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port)
        machineServer = new MachineServerImpl(inetSocketAddress)
        machineServer.start()
    }

    /**
     * 不等待结果返回
     * @param device_id
     * @param message
     * @return
     */
    public boolean sendMessage(String device_id, String message) {
        WebSocket socket = machineServer.getByDeviceId(device_id)
        if (socket == null) {
            return false
        }
        machineServer.getByDeviceId(device_id).send(message)
        return true
    }

    /**
     * 这个方法会等待结果返回
     * 暂定 {_id: '123', action: '123', data: {}}这种形式
     * @param device_id
     * @param message
     * @return
     */
    public Map send(String device_id, Map message) {
        WebSocket socket = machineServer.getByDeviceId(device_id)
        if (socket == null) {
            return null
        }
        def _id = "${device_id}_${System.nanoTime()}".toString()
        try {
            String msg = JSONUtil.beanToJson(message)
            machineServer.getByDeviceId(device_id).send(msg)
            Task task = new Task()
            machineServer.register(_id, task)
            Map result = task.get()
            return result
        } catch (Exception e) {
            logger.error('machine socket server error.' + e)
        } finally {
            machineServer.unregister(_id)
        }
        return null
    }

    //监听事件
    class Task implements Callable<Map>{
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

    }

    class MachineServerImpl extends WebSocketServer {
        private static Map<WebSocket, BasicDBObject> machines = new HashMap<>()
        private static Map<String, WebSocket> devices = new HashMap<>()
        private Map<String, Task> messageListener = new ConcurrentHashMap<>()

        public MachineServerImpl(InetSocketAddress address) {
            super(address)
        }

        DBCollection machine() {
            adminMongo.getCollection('machine')
        }

        public WebSocket getByDeviceId(String device_id) {
            return devices.get(device_id)
        }

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
            if (!keypaire.containsKey('device_id')) {
                conn.send(Result.丢失必需参数.toJsonString())
                conn.close()
                return
            }
            def device_id = keypaire.get('device_id')
            def deviceInfo = machine().findOne($$(_id: device_id)) as BasicDBObject
            if (deviceInfo == null) {
                conn.send(Result.丢失必需参数.toJsonString())
                conn.close()
                return
            }
            //todo 每一步都要记录状态
            deviceInfo['websocket'] = conn
            machines.put(conn, deviceInfo)
            devices.put(device_id, conn)
            conn.send('opening success!')
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            logger.debug("${conn} has left the room!")
            //去掉对应的conn
            if (machines.containsKey(conn)) {
                def machineinfo = machines.remove(conn)
                devices.remove(machineinfo['device_id'])
            }
        }

        //
        @Override
        public void onMessage(WebSocket conn, String message) {
            logger.debug("" + conn + ": " + message)
            Map msg = JSONUtil.jsonToMap(message) ?: [:]
            String _id = (String) msg.get('_id')
            Task task = messageListener.remove(_id)
            if (task != null) {
                task.execute()
            }
            conn.send('received msg.')
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            ex.printStackTrace()
            if (conn != null) {
                // some errors like port binding failed may not be assignable to a specific websocket
                return
            }
            //去掉对应的conn
            if (machines.containsKey(conn)) {
                def machineinfo = machines.remove(conn)
                devices.remove(machineinfo['device_id'])
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
        public void register(String logId, Task task) {
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
        }

        public void unregister(String logId) {
            messageListener.remove(logId)?.cancel()
        }

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

    public static void main(String[] args) throws InterruptedException, IOException {
        WebSocketImpl.DEBUG = true;
        int port = 8887 // 843 flash policy port
        try {
            port = Integer.parseInt(args[0])
        } catch (Exception ex) {
            println ex
        }
        MachineServerService server = new MachineServerService()
        server.hostname = '127.0.0.1'
        server.port = port
        server.init()
        WebSocketServer s = server.machineServer
        s.start()
        System.out.println("ChatServer started on port: " + s.getPort())

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in))
        while (true) {
            String readin = sysin.readLine()
            s.broadcast(readin)
            if (readin.equals("exit")) {
                s.stop(1000)
                break
            }
        }
    }

}
