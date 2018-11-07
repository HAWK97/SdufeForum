package com.hawk.config;

import com.hawk.interceptor.WebSocketInterceptor;
import com.hawk.service.SocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebMvcConfigurer, WebSocketConfigurer {

    @Resource
    private SocketHandler socketHandler;

    // 注册 SocketHandler 与 WebSocketInterceptor
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(socketHandler, "/socketServer")
                .addInterceptors(webSocketInterceptor())
                // 跨域设置，不然会报403错误
                .setAllowedOrigins("http://localhost:63342", "http://localhost:4200", "http://47.106.173.13");
    }

    @Bean
    WebSocketInterceptor webSocketInterceptor() {
        return new WebSocketInterceptor();
    }
}
