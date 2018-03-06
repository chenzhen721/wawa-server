package com.wawa.socket;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

public class VideoSocketServer extends TextWebSocketHandler {

    //todo 这里需要有个数据结构存储对应的推拉流方
    //key为机器号+流名称  value对应session
    Map<String, WebSocketSession> streamMaps = new HashMap<>();



    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        //推流放需要标识下自己，如果为推流方则缓存至map中



        //todo 定期检查socket连接是否可用

    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }

    public static void main(String[] args) {

    }



}
