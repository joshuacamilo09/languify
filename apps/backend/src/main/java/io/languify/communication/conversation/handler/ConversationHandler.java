package io.languify.communication.conversation.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.languify.communication.conversation.dto.StartConversationDTO;
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
  private final ObjectMapper mapper = new ObjectMapper();

  public void handleSegment(String segment, JsonNode data, WebSocketSession session) {
    if (!Objects.equals(segment, "start")) return;
    this.startConversation(data, session);
  }

  private void startConversation(JsonNode data, WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);

    try {
      StartConversationDTO dto = this.mapper.treeToValue(data, StartConversationDTO.class);

      Conversation conversation =
          this.service.createConversation(
              dto.getSourceLanguage(), dto.getTargetLanguage(), s.getUser());
    } catch (Exception e) {
      // TODO: Handle error
    }
  }
}
