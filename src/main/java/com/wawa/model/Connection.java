package com.wawa.model;

import org.springframework.web.socket.WebSocketSession;

public class Connection<T> {

    private String id; //对象的唯一标识
    private WebSocketSession session;
    private T data;

    public Connection(String id, WebSocketSession session, T data) {
        this.id = id;
        this.session = session;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public T getData() {
        return data;
    }
}
