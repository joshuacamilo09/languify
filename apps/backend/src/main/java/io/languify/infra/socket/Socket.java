package io.languify.infra.socket;

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
        .addHandler(this.handler(), "/socket")
        .addInterceptors(this.handshake)
        .setAllowedOrigins("*");
  }

  @Bean
  public WebSocketHandler handler() {
    return new Handler();
  }
}
