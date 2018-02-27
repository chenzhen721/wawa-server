package com.wawa.service

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.wawa.api.Web
import com.wawa.api.event.Task
import com.wawa.common.doc.Result
import com.wawa.common.util.JSONUtil
import groovy.transform.CompileStatic
import org.apache.commons.lang.StringUtils
import org.java_websocket.WebSocket
import org.java_websocket.WebSocketImpl
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static com.wawa.common.util.WebUtils.$$

/**
 * Created by Administrator on 2018/2/27.
 */
@CompileStatic
class MachineServerImpl extends WebSocketServer {
    private static Logger logger = LoggerFactory.getLogger(MachineServerImpl.class)
    private static ExecutorService executor = Executors.newCachedThreadPool()
    private static Map<WebSocket, BasicDBObject> machines = new HashMap<>()
    private static Map<String, WebSocket> devices = new HashMap<>()
    private Map<String, Task> messageListener = new ConcurrentHashMap<>()

    //private MongoTemplate adminMongo = Web.adminMongo
    //private WriteConcern writeConcern = Web.writeConcern

    public MachineServerImpl(InetSocketAddress address) {
        super(address)
        logger.info("=======================> fuck twice!")
    }

    DBCollection machine() {
        Web.adminMongo.getCollection('machine')
    }

    public WebSocket getByDeviceId(String device_id) {
        return devices.get(device_id)
    }


    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info('==========------------------------->>>>WTF!')
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
            task.execute(executor)
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
            Timer timer = new Timer()
            timer.schedule(new TimerTask() {
                @Override
                void run() {
                    Task removal = messageListener.remove(logId)
                    if (removal != null) {
                        task.execute(executor)
                    }
                }
            }, 60 * 1000L)
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
