package com.wawa.web

import com.wawa.api.Web
import com.wawa.base.BaseController
import com.wawa.base.anno.Rest
import com.wawa.common.util.JSONUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest

import static com.wawa.common.util.WebUtils.$$

/**
 * 公共信息接口
 */
@Rest
class PublicController extends BaseController {

    Logger logger = LoggerFactory.getLogger(PublicController.class)

    def blackword_list(HttpServletRequest req) {
        Integer type = Web.firstNumber(req)
        def query = $$('type', $$($in: [type, 2]))   //公共关键字 type:2
        def db_obj = adminMongo.getCollection('blackwords').find(query, $$(_id: 1)).batchSize(5000).toArray()
                .collect { it['_id'] }
        def result = [code: 1, data: db_obj]
        String json = JSONUtil.beanToJson(result)
    }


}