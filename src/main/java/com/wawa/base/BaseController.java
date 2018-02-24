package com.wawa.base;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;
import com.wawa.AppProperties;
import com.wawa.base.support.ControllerSupport7;
import com.wawa.common.util.MsgExecutor;
import com.wawa.model.AppPropType;
import com.wawa.model.User;
import com.wawa.api.DoCost;
import com.wawa.api.Web;
import com.wawa.model.Finance;
import groovy.transform.CompileStatic;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wawa.common.doc.MongoKey.$gte;
import static com.wawa.common.doc.MongoKey.$inc;
import static com.wawa.common.doc.MongoKey.$ne;
import static com.wawa.common.doc.MongoKey.$pull;
import static com.wawa.common.doc.MongoKey.$push;
import static com.wawa.common.doc.MongoKey._id;
import static com.wawa.common.doc.MongoKey.timestamp;
import static com.wawa.common.util.WebUtils.$$;

/**
 * BaseController<
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


