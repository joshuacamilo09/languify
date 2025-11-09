package io.languify.communication.conversation.socket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.service.ConversationService;
import io.languify.communication.conversation.socket.dto.StartConversationDTO;
import io.languify.communication.conversation.socket.state.ConversationStateManager;
import io.languify.identity.auth.model.Session;
import io.languify.infra.realtime.Realtime;
import io.languify.infra.socket.Handler;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class ConversationHandler extends Handler {
  private final ConversationService service;
  private final ConversationStateManager state;
  private final ObjectMapper mapper = new ObjectMapper();

  @Value("${env.OPEN_AI_SECRET}")
  private String secret;

  @Value("${env.OPEN_AI_REALTIME_URI}")
  private String uri;

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

      Realtime realtime = new Realtime(this.secret, this.uri);
      realtime.connect();

      this.state.store(conversation, realtime, session);
      this.emit("conversation:start:success", null, session);
    } catch (Exception e) {
      this.emit("conversation:start:error", null, session);
    }
  }
}
