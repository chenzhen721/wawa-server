package com.wawa.web

import com.wawa.base.BaseController
import com.wawa.base.anno.Rest
import com.wawa.common.doc.Result
import com.wawa.service.VideoServerService
import com.wawa.socket.MessageSocketServer
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.ServletRequestUtils

import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * 公共信息接口
 */
@Rest
class VideoController extends BaseController {

    Logger logger = LoggerFactory.getLogger(VideoController.class)

    @Resource
    VideoServerService videoServerService

    def record_start(HttpServletRequest req) {
        logger.info(System.currentTimeMillis() + ",record_start:" + req.getParameterMap())
        def logId = ServletRequestUtils.getStringParameter(req, 'log_id')
        def deviceId = ServletRequestUtils.getStringParameter(req, 'device_id')
        if (StringUtils.isBlank(logId) || StringUtils.isBlank(deviceId)) {
            return Result.参数错误
        }
        videoServerService.record_start(logId, deviceId)
        return Result.success
    }

    def record_stop(HttpServletRequest req) {
        logger.info("record_stop:" + req.getParameterMap())
        def logId = ServletRequestUtils.getStringParameter(req, 'log_id')
        def deviceId = ServletRequestUtils.getStringParameter(req, 'device_id')
        if (StringUtils.isBlank(logId) || StringUtils.isBlank(deviceId)) {
            return Result.参数错误
        }
        videoServerService.record_stop(logId, deviceId)
        return Result.success
    }

}