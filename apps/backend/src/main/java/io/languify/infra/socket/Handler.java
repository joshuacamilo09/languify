package io.languify.infra.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.languify.communication.conversation.handler.ConversationHandler;
import io.languify.infra.socket.envelopes.MessageEnvelope;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class Handler extends TextWebSocketHandler {
  private final ConversationHandler conversationHandler;
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
    String payload = message.getPayload();
    MessageEnvelope envelope = this.mapper.readValue(payload, MessageEnvelope.class);

    String[] segments = envelope.getEvent().split(":", 2);
    String resource = segments[0];

    if (!Objects.equals(resource, "conversation")) {
      throw new Exception("Invalid event");
    }

    String segment = segments[1];
    conversationHandler.handleSegment(segment, envelope.getData(), session);
  }
}
