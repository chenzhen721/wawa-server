package com.wawa.socket;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.wawa.common.util.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.annotation.Resource;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static com.wawa.common.doc.MongoKey._id;
import static com.wawa.common.util.WebUtils.$$;

public class DollHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    private static Logger logger = LoggerFactory.getLogger(DollHandshakeInterceptor.class);

    @Resource
    public MongoTemplate logMongo;

    DBCollection record_log() {
        return logMongo.getCollection("record_log");
    }

    //加入连接时的校验逻辑
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        super.beforeHandshake(request, response, wsHandler, attributes);
        try {
            URI uri = request.getURI();
            String descriptor = uri.getQuery();
            if (StringUtils.isBlank(descriptor)) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                response.close();
                return false;
            }
            Map<String, String> keypaire = StringHelper.parseUri(descriptor);
            if (keypaire == null) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                response.close();
                return false;
            }
            if (!keypaire.containsKey("log_id")) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                response.close();
                return false;
            }
            String log_id = keypaire.get("log_id");
            Map<String, Object> param = new HashMap<>();
            param.put(_id, log_id);
            param.put("finish_time", $$("$lte", System.currentTimeMillis()));
            DBObject logInfo = record_log().findOne($$(param));
            if (logInfo == null) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                response.close();
                return false;
            }
            attributes.put("logInfo", logInfo);
            return true;
        } catch (Exception e) {
            logger.error("error to handshake." + request, e);
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }

}
