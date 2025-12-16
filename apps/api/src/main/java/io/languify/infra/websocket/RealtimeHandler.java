package io.languify.infra.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class RealtimeHandler extends TextWebSocketHandler {

    @Value("${openai.api.key}")
    private String apiKey;

    private final Map<String, WebSocketSession> openAISessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession userSession) {
        System.out.println("📱 Android conectado: " + userSession.getId());
        connectToOpenAI(userSession);
    }

    @Override
    protected void handleTextMessage(WebSocketSession userSession, TextMessage message) throws Exception {
        WebSocketSession openAISession = openAISessions.get(userSession.getId());

        if (openAISession != null && openAISession.isOpen()) {
            openAISession.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession userSession, CloseStatus status) {
        WebSocketSession openAISession = openAISessions.remove(userSession.getId());
        if (openAISession != null) {
            try { openAISession.close(); } catch (Exception ignored) {}
        }
    }

    private void connectToOpenAI(WebSocketSession userSession) {
        StandardWebSocketClient client = new StandardWebSocketClient();

        String url =
                "wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2025-06-03";

        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("Authorization", "Bearer " + apiKey);
        headers.add("OpenAI-Beta", "realtime=v1");

        TextWebSocketHandler openAIHandler = new TextWebSocketHandler() {

            @Override
            public void afterConnectionEstablished(WebSocketSession openAISession) {
                try {

                    String sessionUpdate = """
                    {
                      "type": "session.update",
                      "session": {
                        "modalities": ["text", "audio"],
                        "voice": "alloy",
                        "output_audio_format": "pcm16",
                        "input_audio_format": "pcm16"
                      }
                    }
                    """;

                    openAISession.sendMessage(new TextMessage(sessionUpdate));


                    String hello = """
                    {
                      "type": "response.create",
                      "response": {
                        "modalities": ["text", "audio"],
                        "instructions": "Olá! O áudio está a funcionar corretamente."
                      }
                    }
                    """;

                    openAISession.sendMessage(new TextMessage(hello));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
                System.out.println("⬅ OpenAI: " + message.getPayload());

                if (userSession.isOpen()) {
                    userSession.sendMessage(message);
                }
            }
        };

        try {
            WebSocketSession openAISession =
                    client.execute(openAIHandler, headers, URI.create(url)).get();

            openAISessions.put(userSession.getId(), openAISession);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            try { userSession.close(CloseStatus.SERVER_ERROR); } catch (Exception ignored) {}
        }
    }
}
