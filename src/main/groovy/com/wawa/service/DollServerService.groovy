package com.wawa.service

import com.google.common.eventbus.Subscribe
import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBObject
import com.mongodb.WriteConcern
import com.wawa.common.util.JSONUtil
import com.wawa.model.ActionTypeEnum
import com.wawa.model.MessageEvent
import com.wawa.socket.DollSocketServer
import com.wawa.socket.WebSocketHelper
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

import javax.annotation.PostConstruct
import javax.annotation.Resource

import static com.wawa.common.doc.MongoKey._id
import static com.wawa.common.util.WebUtils.$$

@CompileStatic
@Component
class DollServerService {
    static final Logger logger = LoggerFactory.getLogger(DollServerService.class)

    @Resource
    public MongoTemplate logMongo
    @Resource
    public WriteConcern writeConcern
    @Resource
    public DollSocketServer dollSocketServer
    @Resource
    private MachineServerService machineServerService

    @PostConstruct
    public void init() {
        dollSocketServer.register(this)
    }

    DBCollection record_log() {
        return logMongo.getCollection("record_log")
    }

    //todo 向服务端发送游戏通知等信息，注册开始结束监听器等
    @Subscribe
    public void listener(MessageEvent messageEvent) {
        //写入properties文件内
        try {
            DBObject playerinfo = (DBObject) messageEvent.getData()
            TextMessage message = messageEvent.getMessage()
            WebSocketSession session = (WebSocketSession)playerinfo.get("player")
            String deviceId = String.valueOf(playerinfo.get("device_id"))
            String status = String.valueOf(playerinfo.get("status"))
            if (!"0".equals(status)) {
                logger.debug("wrong play status:" + status)
                return
            }
            Map msg = JSONUtil.jsonToMap(message.getPayload())
            if (msg == null || !msg.containsKey("action") || !msg.containsKey("data")) {
                return
            }
            String action = String.valueOf(msg.get("action"))
            String data = String.valueOf(msg.get("data"))
            if ("operate".equals(action)) {
                Map<String, Object> req = new HashMap<>()
                req.put("action", ActionTypeEnum.操控指令.getId())
                Map<String, Object> op = new HashMap<>()
                req.put("data", op)
                if ("u".equals(data)) {
                    op.put("FBtime", playerinfo.get("FBtime"))
                    op.put("direction", 0)
                    machineServerService.sendMessage(deviceId, JSONUtil.beanToJson(req))
                    return
                }
                if ("d".equals(data)) {
                    op.put("FBtime", playerinfo.get("FBtime"))
                    op.put("direction", 1)
                    machineServerService.sendMessage(deviceId, JSONUtil.beanToJson(req))
                    return
                }
                if ("l".equals(data)) {
                    op.put("LRtime", playerinfo.get("LRtime"))
                    op.put("direction", 2)
                    machineServerService.sendMessage(deviceId, JSONUtil.beanToJson(req))
                    return
                }
                if ("r".equals(data)) {
                    op.put("LRtime", playerinfo.get("LRtime"))
                    op.put("direction", 3)
                    machineServerService.sendMessage(deviceId, JSONUtil.beanToJson(req))
                    return
                }
                if ("doll".equals(data)) {
                    playerinfo.put("status", 1)
                    op.put("doll", 1)
                    op.put("direction", 8)
                    Map response = machineServerService.send(deviceId, req)
                    Map<String, Object> result = new HashMap<>()
                    result.put("action", "result")
                    if (response == null || response.get("code") == null ||
                            response.get("code") != 1 || response.get("data") == null) {
                        result.put("data", false)
                        WebSocketHelper.send(session, JSONUtil.beanToJson(result))
                    } else {
                        result.put("data", response.get("data"))
                        WebSocketHelper.send(session, JSONUtil.beanToJson(result))
                    }
                    //更新记录
                    BasicDBObject update = $$("status", 1)
                    update.append("result", result.get("data"))
                    record_log().update($$(_id, String.valueOf(playerinfo.get("_id"))), $$($set: update), false, false, writeConcern)
                    session.close()
                }
            }
        } catch (Exception e) {
            logger.error("exception: ${e}".toString())
        }
    }

}
