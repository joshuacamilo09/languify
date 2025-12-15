package io.languify.infra.websocket;

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

    // O teu handler original (Chat Base de Dados)
    private final GlobalHandler globalHandler;

    // O novo handler (Tradução OpenAI)
    private final RealtimeHandler realtimeHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 1. Chat Normal (Mantém como tinhas)
        registry.addHandler(globalHandler, "/ws")
                .addInterceptors(this.handshake)
                .setAllowedOrigins("*");

        // 2. Novo Endpoint para OpenAI
        // O Android deve conectar em: wss://TEU-NGROK-URL/ws/realtime
        // Nota: Permitimos todas as origens (*) para evitar bloqueios do Ngrok
        registry.addHandler(realtimeHandler, "/ws/realtime")
                .setAllowedOrigins("*");
    }
}