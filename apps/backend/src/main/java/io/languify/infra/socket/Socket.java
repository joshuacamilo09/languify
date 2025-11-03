package io.languify.infra.socket;

import io.languify.communication.conversation.handler.ConversationHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class Socket implements WebSocketConfigurer {
  private Handshake handshake;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(conversationHandler(), "/ws/conversation")
        .addInterceptors(this.handshake)
        .setAllowedOrigins("*");
  }

  @Bean
  public WebSocketHandler conversationHandler() {
    return new ConversationHandler();
  }
}
