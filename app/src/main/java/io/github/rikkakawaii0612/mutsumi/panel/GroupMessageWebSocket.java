package io.github.rikkakawaii0612.mutsumi.panel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws/groupMsg")
public class GroupMessageWebSocket {
    private static final Logger LOGGER = LoggerFactory.getLogger("WebSocket");
    // 保存所有连接的会话
    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("WebSocket 连接打开: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        LOGGER.error("Error in WebSocket Server Panel: ", error);
    }

    // 广播消息给所有已连接的浏览器
    public static void broadcast(String jsonMessage) {
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(jsonMessage);
            }
        }
    }
}