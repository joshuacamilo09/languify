package io.languify.infra.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.languify.communication.conversation.controller.websocket.ConversationHandler;
import io.languify.infra.websocket.dto.WebSocketMessageDTO;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class GlobalHandler extends TextWebSocketHandler {
  private final ConversationHandler conversationHandler;
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message)
      throws Exception {
    String payload = message.getPayload();
    WebSocketMessageDTO dto = this.mapper.readValue(payload, WebSocketMessageDTO.class);

    String[] segments = dto.getEvent().split(":", 2);
    String resource = segments[0];

    if (!Objects.equals(resource, "conversation")) {
      return;
    }

    String segment = segments[1];
    conversationHandler.handleSegment(segment, dto.getData(), session);
  }
}
