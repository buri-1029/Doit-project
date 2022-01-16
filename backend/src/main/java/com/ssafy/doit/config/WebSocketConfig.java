package com.ssafy.doit.config;

import com.ssafy.doit.config.handler.StompHandler;
import com.ssafy.doit.service.jwt.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private StompHandler stompHandler;
    @Autowired
    private CookieUtil cookieUtil;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws").setAllowedOrigins("http://i4c108.p.ssafy.io").withSockJS()
                .setInterceptors(httpSessionHandshakeInterceptor());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/publish");
        config.enableSimpleBroker("/subscribe");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(stompHandler);
    }

    @Bean
    public HandshakeInterceptor httpSessionHandshakeInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                if (request instanceof ServletServerHttpRequest) {
                    ServletServerHttpRequest servletServerRequest = (ServletServerHttpRequest) request;
                    HttpServletRequest servletRequest = servletServerRequest.getServletRequest();
                    Cookie token = cookieUtil.getCookie(servletRequest, "refreshToken");
                    attributes.put("refreshToken", token.getValue());
                }
                return true;
            }
            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
            }
        };
    }
}
