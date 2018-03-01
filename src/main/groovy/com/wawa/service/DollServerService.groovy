package com.wawa.service

import com.mongodb.WriteConcern
import com.wawa.socket.DollSocketServer
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@CompileStatic
@Component
class CoreServerService {
    static final Logger logger = LoggerFactory.getLogger(CoreServerService.class)

    @Resource
    public MongoTemplate logMongo
    @Resource
    public WriteConcern writeConcern
    @Resource
    public MachineServerService machineServerService
    @Resource
    public DollSocketServer dollSocketServer

    //todo 向服务端发送游戏通知等信息，注册开始结束监听器等

}
