package com.an.chat.controller;

import com.an.chat.dto.request.IntrospectRequest;
import com.an.chat.dto.response.IntrospectResponse;
import com.an.chat.entity.WebSocketSession;
import com.an.chat.service.IdentityService;
import com.an.chat.service.WebSocketSessionService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

// Class này để quản lý lifecycle của SocketIOServer, start server khi ứng dụng khởi động và stop server khi ứng dụng
// tắt
@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {
    SocketIOServer server;
    IdentityService identityService;
    WebSocketSessionService webSocketSessionService;

    @OnConnect
    public void clientConnected(SocketIOClient socketIOClient) {
        // Get token from request param
        String token = socketIOClient.getHandshakeData().getSingleUrlParam("token");

        // verify token
        IntrospectResponse introspectResponse = identityService.introspect(IntrospectRequest.builder().token(token).build());

        // disconnect if token is invalid
        if (introspectResponse.isValid()){
            log.info("Client connected: " + socketIOClient.getSessionId());

            // persist websocket session
            WebSocketSession webSocketSession = WebSocketSession.builder()
                    .socketSessionId(socketIOClient.getSessionId().toString())
                    .userId(introspectResponse.getUserId())
                    .createdAt(Instant.now())
                    .build();

            webSocketSession = webSocketSessionService.createWebSocketSession(webSocketSession);

            log.info("WebSocket session created: " + webSocketSession.getId());

        } else {
            log.info("Authentication fail " + socketIOClient.getSessionId());
            socketIOClient.disconnect();
        }

    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient socketIOClient) {
        log.info("Client disconnected: " + socketIOClient.getSessionId());
        webSocketSessionService.deleteSocketSession(socketIOClient.getSessionId().toString());
    }

    // start socket server sau khi đã inject bean
    @PostConstruct
    void startServer() {
        server.addListeners(
                this); // để có thể xử lý các sự kiện từ client, ví dụ như khi có client kết nối, gửi tin nhắn, v.v.
        server.start();
        log.info("Socket server started");
    }

    @PreDestroy
    void stopServer() {
        server.stop();
        log.info("Socket server stopped");
    }
}
