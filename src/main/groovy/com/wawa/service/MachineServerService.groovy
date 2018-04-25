package com.wawa.service

import com.wawa.api.event.Task
import com.wawa.common.util.JSONUtil
import com.wawa.model.ActionResult
import com.wawa.model.Response
import com.wawa.socket.WebSocketHelper
import com.wawa.socket.MachineSocketServer
import groovy.transform.CompileStatic
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@CompileStatic
@Slf4j
@Component
class MachineServerService {
    static final Logger logger = LoggerFactory.getLogger(MachineServerService.class)
    @Autowired
    public MachineSocketServer machineServer

    /**
     * 不等待结果返回
     * @param device_id
     * @param message
     * @return
     */
    boolean sendMessage(String device_id, String message) {
        WebSocketSession socket = machineServer.getByDeviceId(device_id)
        if (socket == null) {
            return false
        }
        WebSocketHelper.send(machineServer.getByDeviceId(device_id), message)
        return true
    }

    /**
     * 这个方法会等待结果返回
     * 暂定 {_id: '123', action: '123', data: {}, ts: 123}这种形式
     * @param device_id
     * @param message
     * @return
     */
    Response<ActionResult> send(String device_id, Map message) {
        WebSocketSession socket = machineServer.getByDeviceId(device_id)
        Response resp = new Response()
        resp.setCode(0)
        if (socket == null || !socket.isOpen()) {
            if (socket != null && !socket.isOpen()) {
                machineServer.remove(device_id)
            }
            return resp
        }
        def _id = "${device_id}_${System.nanoTime()}".toString()
        try {
            message.put("id", _id)
            String msg = JSONUtil.beanToJson(message)
            WebSocketHelper.send(machineServer.getByDeviceId(device_id), msg)
            Task task = new Task()
            machineServer.register(_id, task)
            Response<ActionResult> result = task.get()
            return result
        } catch (Exception e) {
            logger.error('machine socket server error.' + e)
        } finally {
            machineServer.unregister(_id)
        }
        return resp
    }

}
