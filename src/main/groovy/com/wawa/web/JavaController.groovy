package com.wawa.web

import com.wawa.api.Web
import com.wawa.base.BaseController
import com.wawa.base.anno.Rest
import com.wawa.common.util.DateUtil
import com.wawa.common.util.DelayQueueRedis
import com.wawa.common.util.MsgDigestUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest

/**
 *
 * 程序之间 集成 胶水代码
 *
 */
@Rest
class JavaController extends BaseController{

    private final static String VERSION = "3.6.0";
    /**
     * 后台变更了用户类型之后 回调
     *
     * @param request
     * @returnL
     */
    static final  Logger logger = LoggerFactory.getLogger(JavaController.class)

    def version(){
        [code: 1,data:[version:VERSION]]
    }

    def ip(HttpServletRequest req){
        [code :1, data:Web.getClientIp(req)]
    }

    def md5(HttpServletRequest req){
        [code :1, data:[md5:MsgDigestUtil.MD5.digest2HEX(req['data'] as String ?: '')]]
    }

    def test (){
        DelayQueueRedis testQueue = DelayQueueRedis.generateQueue("test");
        testQueue.offer(new DelayQueueRedis.Task(UUID.randomUUID().toString(), 10*1000, 10*1000+"后执行"));
        testQueue.addListner(new DelayQueueRedis.DelayQueueJobListener(){
            public void doJob(DelayQueueRedis.Task task){
                logger.debug(task.toString() + " 已经从延时队列中转至队列" + "当前时间:" + DateUtil.getFormatDate(DateUtil.DFMT, System.currentTimeMillis()));
                testQueue.offer(new DelayQueueRedis.Task(UUID.randomUUID().toString(), 10*1000, 10*1000+"后执行"));
            }
        })
    }
}