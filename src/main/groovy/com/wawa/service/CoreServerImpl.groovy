package com.wawa.service

import groovy.transform.CompileStatic

/**
 * Created by Administrator on 2018/2/27.
 */
@CompileStatic
class CoreServerImpl  {
    /*static final Logger logger = LoggerFactory.getLogger(CoreServerService.class)
    private static Map<WebSocket, BasicDBObject> players = new HashMap<>()
    //可以考虑使用eventBus来处理对应的返回值回调问题
    public CoreServerImpl(InetSocketAddress address) {
        super(address)
    }

    DBCollection record_log() {
        Web.logMongo.getCollection('record_log')
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
