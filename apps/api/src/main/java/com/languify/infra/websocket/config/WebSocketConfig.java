package com.languify.infra.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.languify.communication.conversation.handler.ConversationHandler;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
  private final ConversationHandler conversationHandler;
  private final AuthHandshake authHandshake;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(conversationHandler, "/ws/conversation").addInterceptors(authHandshake)
        .setAllowedOrigins("*");
  }
}
