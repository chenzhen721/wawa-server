package com.wawa.socket;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.wawa.common.util.HttpClientUtils;
import com.wawa.common.util.JSONUtil;
import com.wawa.common.util.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.net.URI;
import java.util.Map;

import static com.wawa.common.util.WebUtils.$$;

public class MessageHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    public static final Logger logger = LoggerFactory.getLogger(MessageHandshakeInterceptor.class);

    public static final String user_info = "user/info";

    @Value("#{application['main.domain']}")
    public String mainDomain;

    //todo 加入连接时的校验逻辑
    @Override
    @SuppressWarnings("unchecked")
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        System.out.println("Before Handshake");
        try {
            URI uri = request.getURI();
            String descriptor = uri.getQuery();
            //todo 这些操作都可以放到interceptor中完成
            if (StringUtils.isBlank(descriptor)) {
                return false;
            }
            Map<String, String> keypaire = StringHelper.parseUri(descriptor);
            if (keypaire == null) {
                return false;
            }
            if (!keypaire.containsKey("access_token")) {
                return false;
            }
            String access_token = keypaire.get("access_token");
            String rtv = HttpClientUtils.get(mainDomain + user_info + "/" + access_token, null);
            if (rtv == null || StringUtils.isBlank(rtv) || JSONUtil.jsonToMap(rtv) == null) {
                logger.info("error to get user info by access_token:" + access_token);
                return false;
            }
            Map result = JSONUtil.jsonToMap(rtv);
            if (!"1".equals(String.valueOf(result.get("code")))) {
                logger.info("response error. to get user info");
                return false;
            }
            DBObject user = $$((Map) result.get("data"));
            user.put("current_room_id", keypaire.get("room_id"));
            attributes.put("user", user);

        } catch (Exception e) {
            logger.error("connection failed.");
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        System.out.println("After Handshake");
        super.afterHandshake(request, response, wsHandler, ex);
    }

}
