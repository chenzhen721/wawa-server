package com.wawa.web

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBObject
import com.wawa.AppProperties
import com.wawa.api.Web
import com.wawa.base.BaseController
import com.wawa.base.Crud
import com.wawa.base.anno.Rest
import com.wawa.common.doc.MongoKey
import com.wawa.common.doc.Result
import com.wawa.common.util.JSONUtil
import com.wawa.model.ActionTypeEnum
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
    public static final String APP_ID = "wawa_default" //客户标识
    public static final String APP_SECRET = "ab75e7a2de882107d3bc89948a1baa9e" //分配给客户
    public static final String APP_TOKEN = "9c1b7a6868fd2229d1b62e719665bb0b" //给机器用
    public static final String SERVER_URI = AppProperties.get('server.domain')
    public static final String STREAM_URI = AppProperties.get('stream.domain')
    public static final String API_DOMAIN = AppProperties.get("api.domain")

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
     * //todo 机器注册
     * FBspeed: 100, 0-100
     * LRspeed: 100, 0-100
     * UDspeed: 100, 0-100
     * lightWeight: 10, 0-100
     * heavyWeight: 100, 0-100
     * heavyToLight: 100, 0-255
     * playtime: 40, 5-90
     * exitDirection: 1, 0为前出口， 1为后出口
     * @param req
     * @return
     */
    def machine_on(HttpServletRequest req) {
        logger.info("request ${req.getParameterMap()}".toString())
        //就是机器对应的mac地址
        def _id = ServletRequestUtils.getStringParameter(req, '_id')
        //现场工作人员为机器分配的名称
        def name = ServletRequestUtils.getStringParameter(req, 'name')
        if (StringUtils.isBlank(_id) || StringUtils.isBlank(name)) {
            return Result.error
        }
        def info = machine().findOne($$(_id: _id))
        if (info == null) {
            info = new BasicDBObject(_id: _id, FBspeed: 100, FBtime:100, LRspeed: 100, LRtime:100, UDspeed: 100, lightWeight: 10, heavyWeight: 100, heavyToLight: 100, playtime: 40, exitDirection: 1)
        }
        if (info['timestamp'] == null) {
            info['timestamp'] = System.currentTimeMillis()
        }
        if (info['name'] != name) {
            info['name'] = name
        }
        if (info['app_id'] == null) {
            info['app_id'] = APP_ID
            info['app_token'] = APP_TOKEN
        }
        if (info['server_uri'] == null) {
            info['server_uri'] = SERVER_URI
        }
        if (info['stream_uri'] == null) {
            info['stream_uri'] = STREAM_URI
        }
        def device_comport = ServletRequestUtils.getStringParameter(req, 'comport')
        if (info['comport'] != device_comport) {
            info['comport'] = device_comport
        }
        def camera1 = ServletRequestUtils.getStringParameter(req, 'camera1')
        if (info['camera1'] != camera1) {
            info['camera1'] = camera1
        }
        def camera2 = ServletRequestUtils.getStringParameter(req, 'camera2')
        if (info['camera2'] != camera2) {
            info['camera2'] = camera2
        }
        info['url'] = "${API_DOMAIN}public/machine_on".toString()
        info['online_status'] = 'on'
        info['last_modify'] = System.currentTimeMillis()
        def upadte = $$(info.toMap())
        machine().update($$(_id: _id), upadte, true, false, writeConcern)
        logger.info("success.")
        return [code: 1, data: info]
    }

    //todo 接口都需要加密验证
    /**
     * 1 获取机器列表信息
     * @param req
     * @return
     */
    def list(HttpServletRequest req) {
        def app_id = req.getParameter('app_id')
        Crud.list(req, machine(), $$(app_id: app_id), MongoKey.ALL_FIELD, $$(order: -1)) {
            //顺便每台机器的status, 视频流地址等信息
        }
    }

    /**
     * 获取单个机器的详情，包括机器状态和视频流地址等信息
     * @param req
     */
    def info(HttpServletRequest req) {
        def device_id = req.getParameter('device_id')
        if (StringUtils.isBlank(device_id)) {
            return Result.丢失必需参数
        }
        def info = machine().findOne($$(_id: device_id))
        if (info == null) {
            return Result.丢失必需参数
        }
        def result = serverService.send(device_id, [action: ActionTypeEnum.STATUS.name(), ts: System.currentTimeMillis()])
        info['device_status'] = result['code'] == 0 ? 2 : result['data']
        [code: 1, data: info]
    }

    /**
     * //todo 更新游戏参数
     * FBspeed: 100, 0-100
     LRspeed: 100, 0-100
     UDspeed: 100, 0-100
     lightWeight: 10, 0-100
     heavyWeight: 100, 0-100
     heavyToLight: 100, 0-255
     playtime: 40, 5-90
     exitDirection: 1, 0为前出口， 1为后出口
     * @param req
     */
    def update(HttpServletRequest req) {

    }

    /**
     * 2 请求上机，成功后分配操作地址
     * 请求分配对应的机器
     * @param req
     * @return
     */
    def assign(HttpServletRequest req) {
        def app_id = req.getParameter('app_id')
        def ts = req.getParameter('ts')
        def sign = req.getParameter('sign')
        def record_id = req.getParameter('record_id') //第三方ID
        def user_id = req.getParameter('user_id') //
        def device_id = req.getParameter('device_id') //
        def lightWeight = ServletRequestUtils.getIntParameter(req,'lw') //弱抓力
        def heavyWeight = ServletRequestUtils.getIntParameter(req,'hw') //强抓力
        def heavyToLight = ServletRequestUtils.getIntParameter(req,'htl') //强转弱
        //todo 验签  Result.签名错误

        if (lightWeight == null || lightWeight < 0 || lightWeight > 100) {
            return Result.参数错误
        }
        if (heavyWeight == null || heavyWeight < 0 || heavyWeight > 100) {
            return Result.参数错误
        }
        if (heavyToLight == null || heavyToLight < 0 || heavyToLight > 255) {
            return Result.参数错误
        }
        def info = machine().findOne($$(_id: device_id))
        if (info == null) {
            return Result.丢失必需参数
        }
        //支持断线重连 如果已查询到结果信息且在时间内则直接返回，未查到结果信息生成
        def playtime = info['playtime'] as Long
        def record = record_log().findOne($$(record_id: record_id, timestamp: [$gt: System.currentTimeMillis() - playtime * 1000]))
        if (record != null) {
            return Result.ID重复
        }
        //如果机器状态成功则记录当前结果
        Map result = serverService.send(device_id, [action: ActionTypeEnum.STATUS.name(), ts: System.currentTimeMillis()])
        if (result == null || result.get('code') != 1) {
            return Result.机器非空闲
        }
        def status = result.get('data') as Integer
        if (status != 0) {
            return Result.机器非空闲
        }
        def data = [FBspeed: info['FBspeed'],
                    LRspeed: info['LRspeed'],
                    UDspeed: info['UDspeed'],
                    lightWeight: lightWeight,
                    heavyWeight: heavyWeight,
                    heavyToLight: heavyToLight,
                    playtime: info['playtime'],
                    exitDirection: info['exitDirection']]
        Map resp = serverService.send(device_id, [action: ActionTypeEnum.ASSIGN.name(), data: data, ts: System.currentTimeMillis()])
        if (0 == resp['code']) {
            return Result.error
        }
        def _id = device_id + '_' + System.currentTimeMillis()
        def ws_url = "${info['server_uri']}?device_id=${device_id}&log_id=${_id}".toString()
        record_log().save($$(_id: _id,
                config: data,
                device_id: device_id,
                user_id: user_id,
                record_id: record_id,
                app_id: app_id,
                ws_url: ws_url,
                status: 0, //0 开始 1 结束
                timestamp: System.currentTimeMillis()))
        return [code: 1, data: [status: status, ws_url: ws_url, log_id: _id]]
    }

}