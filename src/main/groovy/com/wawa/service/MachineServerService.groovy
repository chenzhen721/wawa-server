package com.wawa.service

import com.wawa.api.event.Task
import com.wawa.common.util.JSONUtil
import groovy.transform.CompileStatic
import lombok.extern.slf4j.Slf4j
import org.java_websocket.WebSocket
import org.java_websocket.WebSocketImpl
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@CompileStatic
@Slf4j
@Component
class MachineServerService {
    static final Logger logger = LoggerFactory.getLogger(MachineServerService.class)

    @Value('#{application[\'machine.hostname\']}')
    public String hostname
    @Value('#{application[\'machine.port\']}')
    public int port

    public MachineServerImpl machineServer

    @PostConstruct
    void init() {
        logger.info("============--------------fuck!")
        try {
            WebSocketImpl.DEBUG = true
            InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port)
            machineServer = new MachineServerImpl(inetSocketAddress)
            machineServer.start()
        } catch (Exception e) {
            logger.info("init server failed." + e)
        }
        logger.info("============--------------fuck end!")
    }

    /**
     * 不等待结果返回
     * @param device_id
     * @param message
     * @return
     */
    boolean sendMessage(String device_id, String message) {
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
    Map send(String device_id, Map message) {
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

}
