package com.an.chat.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.corundumstudio.socketio.SocketIOServer;

// class này để cấu hình SocketIOServer, tạo một bean để có thể inject vào các service khác
@Configuration
public class SocketIOConfig {
    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname("localhost");
        config.setPort(8099);
        config.setOrigin("*"); // Cho phép tất cả các origin kết nối, có thể cấu hình lại để chỉ cho phép origin của FE
        return new SocketIOServer(config);
    }
}
