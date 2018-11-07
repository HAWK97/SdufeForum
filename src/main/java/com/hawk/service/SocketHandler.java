package com.hawk.service;

import com.hawk.data.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SocketHandler implements WebSocketHandler {

    private static final ConcurrentHashMap<Long, WebSocketSession> users = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        Long userId = ((User) webSocketSession.getAttributes().get("user")).getId();
        log.info("用户" + userId.toString() + "成功建立WebSocket连接");
        users.put(userId, webSocketSession);
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        Long userId = ((User) webSocketSession.getAttributes().get("user")).getId();
        log.error("用户" + userId.toString() + "连接出现错误：" + throwable.getMessage());
        users.remove(userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        Long userId = ((User) webSocketSession.getAttributes().get("user")).getId();
        log.debug("用户" + userId.toString() + "WebSocket连接已关闭");
        users.remove(userId);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void sendMessageToUser(Long userId, TextMessage message) {
        for (Map.Entry<Long, WebSocketSession> entry : users.entrySet()) {
            if (entry.getKey().equals(userId)) {
                try {
                    WebSocketSession session = entry.getValue();
                    if (session.isOpen()) {
                        session.sendMessage(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
