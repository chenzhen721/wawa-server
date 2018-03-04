package com.wawa.model;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public class MessageEvent {
    private WebSocketSession session;
    private TextMessage message;
    private Object data;

    public MessageEvent(WebSocketSession session, TextMessage textMessage, Object data) {
        this.session = session;
        this.message = textMessage;
        this.data = data;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public TextMessage getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "MessageEvent{" +
                "session=" + session +
                ", message=" + message +
                ", data=" + data +
                '}';
    }
}
