package org.practica.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);


    private final AuthChannelInterceptorAdapter authChannelInterceptorAdapter;

    public WebSocketConfig(AuthChannelInterceptorAdapter authChannelInterceptorAdapter) {
        this.authChannelInterceptorAdapter = authChannelInterceptorAdapter;

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        logger.info("Registrando endpoint /websocket");
        registry.addEndpoint("/websocket")

                .withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry messageConfig) {
        logger.info("Configurando message broker");
        messageConfig.enableSimpleBroker("/topic");
        messageConfig.setApplicationDestinationPrefixes("/app");
        messageConfig.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptorAdapter);
    }



}
