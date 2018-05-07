package com.wawa.socket;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.wawa.common.doc.Result;
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
import java.util.Map;

import static com.wawa.common.doc.MongoKey._id;
import static com.wawa.common.util.WebUtils.$$;

public class MachineHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    private static Logger logger = LoggerFactory.getLogger(MachineHandshakeInterceptor.class);

    @Resource
    private MongoTemplate adminMongo;
    DBCollection machine() {
        return adminMongo.getCollection("machine");
    }
    //todo 加入连接时的校验逻辑
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        System.out.println("Before Handshake");
        super.beforeHandshake(request, response, wsHandler, attributes);
        try {
            URI uri = request.getURI();
            String descriptor = uri.getQuery();
            //这些操作都可以放到interceptor中完成
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
            if (!keypaire.containsKey("device_id")) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                response.close();
                return false;
            }
            String device_id = keypaire.get("device_id");
            //这里的device_id是根据mac生成的，如果没有的机器要考虑下掉
            DBObject deviceInfo = machine().findOne($$(_id, device_id));
            if (deviceInfo == null) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                response.close();
                return false;
            }
            //deviceInfo.put("websocket", session);
            //machines.put(session.getId(), deviceInfo);
            //devices.put(device_id, session);
            attributes.put("deviceInfo", deviceInfo);
            return true;
        } catch (Exception e) {
            logger.error("connection failed." + request, e);
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        System.out.println("After Handshake");
        super.afterHandshake(request, response, wsHandler, ex);
    }

}
