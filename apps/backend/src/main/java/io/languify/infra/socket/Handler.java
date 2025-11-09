package io.languify.infra.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.languify.communication.conversation.handler.ConversationHandler;
import io.languify.identity.auth.model.Session;
import io.languify.infra.socket.envelopes.MessageEnvelope;
import java.util.Objects;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class Handler extends TextWebSocketHandler {
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    Session s = (Session) session.getAttributes().get("session");

    String payload = message.getPayload();
    MessageEnvelope envelope = this.mapper.readValue(payload, MessageEnvelope.class);

    String[] segments = envelope.getEvent().split(":", 2);

    String resource = segments[0];

    if (!Objects.equals(resource, "conversation")) {
      throw new Exception("Invalid event");
    }

    String segment = segments[1];

    ConversationHandler handler = new ConversationHandler(s);
    handler.handleSegment(segment);
  }
}
