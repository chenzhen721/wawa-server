package com.wawa.service

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
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
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

import static com.wawa.common.util.WebUtils.$$

@Component
class ServerService {
    static final Logger logger = LoggerFactory.getLogger(ServerService.class)

    @Resource
    public MongoTemplate adminMongo
    @Resource
    public WriteConcern writeConcern
    @Value('#{application[\'machine.hostname\']}')
    public String hostname
    @Value('#{application[\'machine.port\']}')
    public int port

    private MachineServerImpl machineServer

    @PostConstruct
    public void init() {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port)
        machineServer = new MachineServerImpl(inetSocketAddress)
        machineServer.start()
    }

    public boolean sendMessage(String device_id, String message) {
        WebSocket socket = machineServer.getByDeviceId(device_id)
        if (socket == null) {
            return false
        }
        machineServer.getByDeviceId(device_id).send(message)
        return true
    }

    public Map send(String device_id, String message) {
        //todo send


        //todo 注册监听事件，收到监听事件返回结果

        //todo 一旦返回结果或者超市要把监听事件取消掉


    }

    //监听事件
    class Task implements Callable<Map>{
        public static ExecutorService executor = Executors.newCachedThreadPool()
        private FutureTask futureTask
        private Map result

        @Override
        Map call() throws Exception {
            return result
        }

        @Subscribe
        public void func(Map msg) {
            System.out.println("map msg: " + msg)
            result = msg
            executor.submit(futureTask)
        }

        public Map get() {
            futureTask = new FutureTask(this)
            futureTask.get(10000l, TimeUnit.MILLISECONDS)
        }

    }


    class MachineServerImpl extends WebSocketServer {
        private static Map<WebSocket, BasicDBObject> machines = new HashMap<>()
        private static Map<String, WebSocket> devices = new HashMap<>()
        private EventBus eventBus = new EventBus()

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
            //broadcast("new connection: " + handshake.getResourceDescriptor());
            //System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
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
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            //broadcast('' + conn + " has left the room!");
            System.out.println("${conn} has left the room!")
            //去掉对应的conn
            if (machines.containsKey(conn)) {
                def machineinfo = machines.remove(conn)
                devices.remove(machineinfo['device_id'])
            }
        }

        //todo 要做好返回值
        @Override
        public void onMessage(WebSocket conn, String message) {
            //broadcast(message);
            System.out.println(conn + ": " + message)
            Map msg = JSONUtil.jsonToMap(message)

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
        public void register(Object event) {
            eventBus.register(event)
        }

        public void unregister(Object event) {
            eventBus.unregister(event)
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
        int port = 8887; // 843 flash policy port
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ex) {
        }
        InetSocketAddress inetAddress = new InetSocketAddress("", port);
        com.socket.MachineSocketServer s = new com.socket.MachineSocketServer(inetAddress);
        s.start();
        System.out.println("ChatServer started on port: " + s.getPort());

        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String readin = sysin.readLine();
            s.broadcast(readin);
            if (readin.equals("exit")) {
                s.stop(1000);
                break;
            }
        }


        FutureTask futureTask = new FutureTask(new Callable<String>() {
            @Override
            String call() throws Exception {
                return "123"
            }
        })
        new Thread(new Runnable() {
            @Override
            void run() {
                println futureTask.get()
            }
        }).start()

        new Thread(new Runnable() {
            @Override
            void run() {
                println 'waiting'
                Thread.sleep(2000l)
                println 'execute'
                ExecutorService service = Executors.newFixedThreadPool(1)
                service.submit(futureTask)
            }
        }).start()

    }

}
