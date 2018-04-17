package com.wawa.web

import com.wawa.base.BaseController
import com.wawa.base.anno.Rest
import com.wawa.common.doc.Result
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
class MessageController extends BaseController {

    Logger logger = LoggerFactory.getLogger(MessageController.class)

    @Resource
    MessageSocketServer messageSocketServer

    def user_message(HttpServletRequest req) {
        logger.info(System.currentTimeMillis() + ",user_message:" + req.getParameterMap())
        def user_id = ServletRequestUtils.getIntParameter(req, 'user_id')
        if (user_id == null) {
            return Result.参数错误
        }
        //向对应房间发送消息
        String str = readJsonString(req)
        logger.info("user_id:" + user_id + ",message:" + str)
        if (StringUtils.isNotBlank(str)) {
            messageSocketServer.sendToUser("" + user_id, str)
        }
        return Result.success
    }

    def room_message(HttpServletRequest req) {
        logger.info("room_message:" + req.getParameterMap())
        def room_id = ServletRequestUtils.getIntParameter(req, 'room_id')
        if (room_id == null) {
            return Result.参数错误
        }
        //向对应房间发送消息
        String str = readJsonString(req)
        logger.info("room_id:" + room_id + ",message:" + str)
        if (StringUtils.isNotBlank(str)) {
            messageSocketServer.sendToRoom("" + room_id, str)
        }
        return Result.success
    }

    def global_message(HttpServletRequest req) {
        String str = readJsonString(req)
        logger.info("global_message:" + str)
        if (StringUtils.isNotBlank(str)) {
            messageSocketServer.sendToGlobal(str)
        }
        return Result.success
    }

    private String readJsonString(HttpServletRequest req) {
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder()
            String inputStr
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr)
            }
            return responseStrBuilder
        } catch (Exception e) {
            logger.error("error read jsonstr." + e)
        }
        return null;
    }
}