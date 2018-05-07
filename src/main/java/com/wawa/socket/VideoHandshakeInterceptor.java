package com.wawa.socket;

import com.wawa.common.doc.Result;
import com.wawa.common.util.StringHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.net.URI;
import java.util.Map;

public class VideoHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    public static final Logger logger = LoggerFactory.getLogger(MessageHandshakeInterceptor.class);

    //加入连接时的校验逻辑
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        System.out.println("Before Handshake");
        super.beforeHandshake(request, response, wsHandler, attributes);
        try {
            URI uri = request.getURI();
            String path = uri.getPath();

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
            if (!keypaire.containsKey("device_id") || !keypaire.containsKey("stream")) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                response.close();
                return false;
            }
            String deviceId = keypaire.get("device_id");
            String stream = !"1".equals(keypaire.get("stream")) ? "2" : "1";
            attributes.put("deviceId", deviceId);
            attributes.put("stream", stream);
            attributes.put("isPush", path.endsWith("push"));
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
