package com.wawa.api;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.WriteConcern;
import com.wawa.AppProperties;
import com.wawa.base.StaticSpring;
import com.wawa.api.interceptor.OAuth2SimpleInterceptor;
import com.wawa.common.doc.Level;
import com.wawa.common.doc.MongoKey;
import com.wawa.common.doc.Param;
import com.wawa.common.util.KeyUtils;
import com.wawa.common.util.WebUtils;
import com.wawa.base.ext.RestExtension;
import com.wawa.model.BlackListType;
import com.wawa.model.PlatformType;
import com.wawa.model.StatusType;
import com.wawa.model.User;
import com.wawa.model.UserAwardType;
import com.wawa.model.UserType;
import groovy.transform.CompileStatic;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.SortParameters;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.ServletRequestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.wawa.common.doc.MongoKey.*;

@CompileStatic
public abstract class Web  extends WebUtils{

    @Resource
    public static WriteConcern writeConcern;

    public static final StringRedisTemplate mainRedis = (StringRedisTemplate) StaticSpring.get("mainRedis");
    public static final StringRedisTemplate userRedis = (StringRedisTemplate) StaticSpring.get("userRedis");
    public static final MongoTemplate mainMongo = (MongoTemplate) StaticSpring.get("mainMongo");
    public static final MongoTemplate adminMongo = (MongoTemplate) StaticSpring.get("adminMongo");

    public static final boolean isTest = AppProperties.get("api.domain").contains("test-");

    public static Integer secondNumber(HttpServletRequest request){
        Integer secondNumber = 0 ;
        try{
            String id2 = request.getParameter(Param.second);
            if(StringUtils.isNotBlank(id2))
                secondNumber = Integer.valueOf(id2);
        }catch(Exception ex){
            logger.error("secondNumber String cast Integer Exception");
        }
        return  secondNumber ;
    }

    public static Integer firstNumber(HttpServletRequest request){
        Integer firstNumber = 0 ;
        try{
            String id1 = request.getParameter(Param.first) ;
            if(StringUtils.isNotBlank(id1))
                firstNumber = Integer.valueOf(id1);
        }catch(Exception ex){
            logger.error("firstNumber String cast Integer Exception, {}", ex);
        }
        return  firstNumber ;
    }

    public static String firstParam(HttpServletRequest request){
        return request.getParameter(Param.first);
    }

    public static String secondParam(HttpServletRequest request){
        return request.getParameter(Param.second);
    }

    public final static DBObject user_field = $$("nick_name",1)
            .append("pic", 1)
            .append("priv", 1)
            .append("level", 1)
            .append("family", 1)
            .append("vip_level", 1)
            .append("exp",1);
    /**
     * 获得用户前端展示信息
     * @param userId
     * @return
     */
    public static DBObject getUserInfo(Integer userId){
        if(userId == null){
            return new BasicDBObject();
        }
        return mainMongo.getCollection("users").findOne(userId, user_field);
    }

    final static  Logger logger = LoggerFactory.getLogger(Web.class) ;

    public static Map getSession(){
        return OAuth2SimpleInterceptor.getSession();
    }

    /**
     * 是否包含黑白名单
     * @param userId
     * @param blackListType
     * @return
     */
    public static boolean isInBlackList(Integer userId, BlackListType blackListType){
        return isInBlackList(userId + "_" + blackListType.ordinal());
    }

    /**
     * 是否包含黑白名单
     */
    public static boolean isInBlackList(String bid){
        return adminMongo.getCollection("blacklist").count($$(_id, bid)) >= 1;
    }

    public static String getClientId(HttpServletRequest req){
        String client_id = req.getParameter(Param.uid);
        if(StringUtils.isNotBlank(client_id)){
            return client_id;
        }
        return getClientIp(req);
    }

    public static String getClientIp(HttpServletRequest req){
        /*String ip = req.getHeader(Param.XFF);
        if(StringUtils.isBlank(ip)){
            ip = req.getRemoteAddr();
        }
        ip = StringUtils.remove(ip, ", 192.168.2.20");
        ip = StringUtils.remove(ip, ", 192.168.2.21");*/
        return getIpAddr(req);
    }

    private static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");//先从nginx自定义配置获取
        //logger.info("X-Forwarded-For: {}", ip);
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            //logger.info("X-Real-IP: {}", ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("PROXY_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {

            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        //ip = StringUtils.remove(ip, ", 192.168.2.20");
        //ip = StringUtils.remove(ip, ", 192.168.2.21");
        //logger.info(">>>>>>>>>>>>> Ip: {}", ip);
        //如果为多个切成1个
        ip = StringUtils.substringBefore(ip, ",");
        return ip;
    }


    public static final Long THREE_DAY_SECONDS = 3 * 24 * 3600L;

    public static Map<String, String> refreshUserInfoOfSession(HttpServletRequest req, Integer userId, DBObject user){
        String access_token = OAuth2SimpleInterceptor.parseToken(req);
        return refreshUserInfoOfSession(access_token, userId, user);
    }

    public static Map<String, String> refreshUserInfoOfSession(String access_token, Integer userId, DBObject user){
        Map<String, String> hashResult = new HashMap<>();
        if(StringUtils.isEmpty(access_token)){
            access_token = userRedis.opsForValue().get(KeyUtils.USER.token(userId));
            if(StringUtils.isEmpty(access_token)){
                return hashResult;
            }
        }
        if(user == null){
            user = getUserInfo(userId);
        }
        hashResult.put("_id", userId.toString());
        hashResult.put("nick_name", (String) user.get("nick_name"));
        hashResult.put("priv", user.get("priv").toString());
        hashResult.put("pic", String.valueOf(user.get("pic")));
        hashResult.put("level", String.valueOf(user.get("level")));
        Map family = (Map) user.get("family");
        if(family != null){
            hashResult.put("family_name", String.valueOf(family.get("name")));
            hashResult.put("family_badge", String.valueOf(family.get("badge")));
            hashResult.put("family_id", String.valueOf(family.get("family_id")));
            hashResult.put("family_priv", String.valueOf(family.get("family_priv")));
        }
        String token_key = KeyUtils.accessToken(access_token);
        if(userRedis.hasKey(token_key)){
            userRedis.delete(token_key);
        }
        userRedis.opsForHash().putAll(token_key, hashResult);
        if(userRedis.getExpire(token_key) < 30){
            userRedis.expire(token_key, THREE_DAY_SECONDS, TimeUnit.SECONDS);
            userRedis.opsForValue().set(KeyUtils.USER.token(userId), access_token, THREE_DAY_SECONDS, TimeUnit.SECONDS);
        }
        return hashResult;
    }

    public static final Integer RobotId = 1024000;  //机器人id



}
