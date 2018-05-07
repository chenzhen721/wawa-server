package com.wawa.service

import com.mongodb.DBCollection
import com.wawa.socket.VideoSocketServer
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@CompileStatic
@Component
class VideoServerService {
    static final Logger logger = LoggerFactory.getLogger(VideoServerService.class)

    @Resource
    public VideoSocketServer videoSocketServer

    @PostConstruct
    public void init() {
        //dollSocketServer.register(this)
    }

    DBCollection record_log() {
        //return logMongo.getCollection("record_log")
    }

    public boolean record_start(String logId, String deviceId) {
        return videoSocketServer.record_open(logId, deviceId);
    }


    public boolean record_stop(String logId, String deviceId) {
        //这个方法调用后 需要把裸码转为对应的MP4码
        return videoSocketServer.record_off(logId, deviceId)
    }
}
