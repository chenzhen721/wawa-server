package com.wawa.service

import com.mongodb.WriteConcern
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


}
