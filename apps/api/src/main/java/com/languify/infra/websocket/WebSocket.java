package com.languify.infra.websocket;

import com.languify.infra.security.config.CorsConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocket implements WebSocketConfigurer {
  private final Handshake handshake;
  private final GlobalHandler handler;
  private final CorsConfig corsConfig;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(handler, "/ws")
        .addInterceptors(this.handshake)
        .setAllowedOrigins(corsConfig.getAllowedOriginsArray());
  }
}
