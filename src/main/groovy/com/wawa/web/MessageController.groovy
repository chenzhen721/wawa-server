package com.wawa.web

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBObject
import com.wawa.AppProperties
import com.wawa.api.Web
import com.wawa.base.BaseController
import com.wawa.base.Crud
import com.wawa.base.anno.Rest
import com.wawa.common.doc.Result
import com.wawa.common.util.JSONUtil
import com.wawa.model.ActionTypeEnum
import com.wawa.service.MachineServerService
import com.wawa.socket.MessageSocketServer
import org.apache.commons.lang.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.ServletRequestUtils

import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

import static com.wawa.common.util.WebUtils.$$

/**
 * 公共信息接口
 */
@Rest
class MessageController extends BaseController {

    Logger logger = LoggerFactory.getLogger(MessageController.class)

    @Resource
    MessageSocketServer messageSocketServer

    def room_message(HttpServletRequest req) {
        def room_id = ServletRequestUtils.getIntParameter(req, 'room_id')
        if (room_id == null) {
            return Result.参数错误
        }
        //向对应房间发送消息
    }

    def global_message(HttpServletRequest req) {

    }


}