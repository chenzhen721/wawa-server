package com.wawa.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WebSocketHelper {
    private static Logger logger = LoggerFactory.getLogger(WebSocketHelper.class);

    public static void send(WebSocketSession session, String message) {
        if (message == null) {
            return;
        }
        TextMessage textMessage = new TextMessage(message);
        try {
            session.sendMessage(textMessage);
        } catch (IOException e) {
            logger.error("send message error." + e);
        }
    }
}
