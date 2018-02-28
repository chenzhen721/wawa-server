package com.wawa.socket;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.wawa.api.Web;
import com.wawa.api.event.Task;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebsocketEndPoint extends TextWebSocketHandler {

    private static Logger logger = LoggerFactory.getLogger(WebsocketEndPoint.class);
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static Map<WebSocket, BasicDBObject> machines = new HashMap<>();
    private static Map<String, WebSocket> devices = new HashMap<>();
    private Map<String, Task> messageListener = new ConcurrentHashMap<>();

    DBCollection machine() {
        return Web.adminMongo.getCollection("machine");
    }

    public WebSocket getByDeviceId(String device_id) {
        return devices.get(device_id);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        TextMessage returnMessage = new TextMessage(message.getPayload()+" received at server");
        session.sendMessage(returnMessage);
    }

}
