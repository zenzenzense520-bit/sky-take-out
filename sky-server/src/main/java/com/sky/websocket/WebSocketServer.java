package com.sky.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket 服务端
 * 管理端来电提醒、订单状态推送通过此端点向指定客户端发送消息
 */
@Component
@ServerEndpoint("/ws/{userId}")
@Slf4j
public class WebSocketServer {

    /** 在线会话：用户标识 -> 会话 */
    private static final Map<String, Session> SESSION_MAP = new HashMap<>();

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        SESSION_MAP.put(userId, session);
        log.info("WebSocket 客户端连接: userId={}, 当前在线数={}", userId, SESSION_MAP.size());
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId) {
        SESSION_MAP.remove(userId);
        log.info("WebSocket 客户端断开: userId={}, 当前在线数={}", userId, SESSION_MAP.size());
    }

    @OnError
    public void onError(@PathParam("userId") String userId, Throwable error) {
        log.error("WebSocket 发生异常: userId={}", userId, error);
    }

    /**
     * 向指定客户端发送消息
     */
    public void sendMessage(String userId, String message) {
        Session session = SESSION_MAP.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("WebSocket 消息发送失败: userId={}", userId, e);
            }
        }
    }

    /**
     * 向全部在线客户端广播消息
     */
    public void sendMessageToAll(String message) {
        Collection<Session> sessions = SESSION_MAP.values();
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    log.error("WebSocket 广播消息失败", e);
                }
            }
        }
    }
}
