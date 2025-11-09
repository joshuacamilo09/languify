package io.languify.communication.conversation.handler;

import com.fasterxml.jackson.databind.JsonNode;
import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.service.ConversationService;
import io.languify.identity.auth.model.Session;
import io.languify.infra.socket.Handler;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class ConversationHandler extends Handler {
  private final ConversationService service;

  public void handleSegment(String segment, JsonNode data, WebSocketSession session) {
    if (!Objects.equals(segment, "start")) return;
    this.startConversation(session);
  }

  private void startConversation(WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    Conversation conversation = this.service.createConversation("Untitled", s.getUser());
  }
}
