package com.ws.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Configuration
    @EnableWebSocket
    static class WsConfig implements WebSocketConfigurer {

        @Bean
        public WsHandler wsHandler() {
            return new WsHandler();
        }

        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
            webSocketHandlerRegistry.addHandler(wsHandler(), "/ws-endpoint");
        }

        @Bean
        public ServletServerContainerFactoryBean createWebSocketContainer() {
            ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
            container.setMaxTextMessageBufferSize(8_000);
            return container;
        }
    }
}
