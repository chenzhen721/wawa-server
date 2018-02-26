package com.wawa.web

import com.mongodb.DBCollection
import com.wawa.api.Web
import com.wawa.base.BaseController
import com.wawa.base.anno.Rest
import com.wawa.common.doc.Result
import com.wawa.common.util.JSONUtil
import com.wawa.service.MachineServerService
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
class PublicController extends BaseController {

    Logger logger = LoggerFactory.getLogger(PublicController.class)

    @Resource
    MachineServerService serverService

    DBCollection machine() {
        adminMongo.getCollection('machine')
    }
    DBCollection record_log() {
        logMongo.getCollection('record_log')
    }

    def blackword_list(HttpServletRequest req) {
        Integer type = Web.firstNumber(req)
        def query = $$('type', $$($in: [type, 2]))   //公共关键字 type:2
        def db_obj = adminMongo.getCollection('blackwords').find(query, $$(_id: 1)).batchSize(5000).toArray()
                .collect { it['_id'] }
        def result = [code: 1, data: db_obj]
        String json = JSONUtil.beanToJson(result)
    }

    /**
     * 机器注册
     * @param req
     * @return
     */
    def machine_on(HttpServletRequest req) {
        def device_id = ServletRequestUtils.getStringParameter(req, 'device_id')
        def device_name = ServletRequestUtils.getStringParameter(req, 'device_name')
        if (StringUtils.isBlank(device_id) || StringUtils.isBlank(device_name)) {
            return Result.error
        }
        def upadte = $$(_id: device_id, device_name: device_name, timestamp: System.currentTimeMillis())
        machine().update($$(device_id: device_id), upadte, true, false, writeConcern)
        return Result.success
    }

    /**
     * 请求分配对应的机器
     * @param req
     * @return
     */
    def assign(HttpServletRequest req) {
        def app_id = req.getParameter('app_id')
        def ts = req.getParameter('ts')
        def sign = req.getParameter('sign')
        def record_id = req.getParameter('record_id') //第三方ID
        def user_id = req.getParameter('user_id') //第三方ID
        def device_id = req.getParameter('device_id') //第三方ID
        //todo 传入各种强力抓信息，有服务器来完成这个操作
        //todo 获取sign

        //如果校验通过，记录本次请求
        //service.getStatus
        //如果机器状态不对则直接返回失败

        //如果机器状态成功则记录当前结果
        //def log_id =
        Map result = serverService.send(device_id, [action: 'status', ts: System.currentTimeMillis()])
        if (result.get('code') != 1) {
            return Result.error
        }
        def status = result.get('data') as Integer
        if (status != 0) {
            return [code: 1, data: [status: status]]
        }
        //todo 记录本次上机请求的参数
        def _id = device_id + '_' + System.currentTimeMillis()
        record_log().save($$(_id: _id,timestamp: System.currentTimeMillis()))
        return [code: 1, data: [status: status, ws_url: 'ws://localhost:8887?device_id=', log_id: _id]]
    }


}