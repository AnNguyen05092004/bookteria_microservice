package com.an.chat.controller;

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

// Class này để quản lý lifecycle của SocketIOServer, start server khi ứng dụng khởi động và stop server khi ứng dụng
// tắt
@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SocketHandler {
    SocketIOServer server;

    @OnConnect
    public void clientConnected(SocketIOClient socketIOClient) {
        log.info("Client connected: " + socketIOClient.getSessionId());
    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient socketIOClient) {
        log.info("Client disconnected: " + socketIOClient.getSessionId());
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
