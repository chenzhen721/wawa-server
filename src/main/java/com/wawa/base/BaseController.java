package com.wawa.base;

import com.mongodb.WriteConcern;
import com.wawa.AppProperties;
import com.wawa.base.support.ControllerSupport7;
import com.wawa.api.Web;
import groovy.transform.CompileStatic;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

import static com.wawa.common.util.WebUtils.$$;

/**
 * BaseController
 */
@CompileStatic
@Slf4j
public abstract class BaseController extends ControllerSupport7 {

    @Resource
    public MongoTemplate mainMongo;
    @Resource
    public MongoTemplate adminMongo;
    @Resource
    public MongoTemplate logMongo;

    public static final StringRedisTemplate mainRedis = Web.mainRedis;

    static final Logger logs = LoggerFactory.getLogger(BaseController.class);
    @Resource
    public WriteConcern writeConcern;


    public static final boolean isTest = AppProperties.get("api.domain").contains("test-");

}


