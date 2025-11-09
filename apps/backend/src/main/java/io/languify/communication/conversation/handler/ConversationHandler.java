package io.languify.communication.conversation.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.service.ConversationService;
import io.languify.identity.auth.model.Session;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class ConversationHandler {
  private final ConversationService service;

  public void handleSegment(String segment, JsonNode data, WebSocketSession webSocketSession) {
    Session session = (Session) webSocketSession.getAttributes().get("session");
    if (!Objects.equals(segment, "start")) return;

    this.startConversation(data, session);
  }

  private void startConversation(JsonNode data, Session session) {
    Conversation conversation = this.service.createConversation("Untitled", session.getUser());
  }
}
