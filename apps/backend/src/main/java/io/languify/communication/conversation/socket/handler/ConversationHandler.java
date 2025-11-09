package io.languify.communication.conversation.socket.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.service.ConversationService;
import io.languify.communication.conversation.socket.dto.ProcessConversationDataDTO;
import io.languify.communication.conversation.socket.dto.StartConversationDTO;
import io.languify.communication.conversation.socket.dto.TranslateConversationDTO;
import io.languify.communication.conversation.socket.state.ConversationState;
import io.languify.communication.conversation.socket.state.ConversationStateManager;
import io.languify.identity.auth.model.Session;
import io.languify.identity.user.model.User;
import io.languify.infra.realtime.Realtime;
import io.languify.infra.socket.Handler;
import java.util.Optional;
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
    Session s = this.extractSessionFromWebSocketSession(session);
    User user = s.getUser();

    switch (segment) {
      case "start":
        try {
          StartConversationDTO dto = this.mapper.treeToValue(data, StartConversationDTO.class);
          this.startConversation(dto, session);
        } catch (Exception e) {
          this.emit("conversation:start:error", null, user.getId(), session);
        }

        break;
      case "data":
        try {
          ProcessConversationDataDTO dto =
              this.mapper.treeToValue(data, ProcessConversationDataDTO.class);
          this.processConversationData(dto, session);
        } catch (Exception e) {
          this.emit("conversation:data:error", null, user.getId(), session);
        }

        break;
      case "translate":
        try {
          TranslateConversationDTO dto =
              this.mapper.treeToValue(data, TranslateConversationDTO.class);

          this.translateConversation(dto, session);
        } catch (Exception e) {
          this.emit("conversation:translate:error", null, user.getId(), session);
        }

        break;
      case "close":
        this.closeConversation(session);
    }
  }

  private void closeConversation(WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    String userId = s.getUser().getId();
    Optional<ConversationState> optionalState = this.state.get(userId);

    if (optionalState.isEmpty()) {
      this.emit("conversation:close:error", null, userId, session);
      return;
    }

    ConversationState state = optionalState.get();

    state.getRealtime().disconnect();
    this.state.remove(userId);

    this.emit("conversation:close:success", null, userId, session);
  }

  private void translateConversation(TranslateConversationDTO data, WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    String userId = s.getUser().getId();

    Optional<ConversationState> optionalState = this.state.get(userId);

    if (optionalState.isEmpty()) {
      this.emit("conversation:translate:error", null, userId, session);
      return;
    }

    ConversationState state = optionalState.get();
    state.getRealtime().commitAndTranslate(data.getFromLanguage(), data.getToLanguage());
  }

  private void processConversationData(ProcessConversationDataDTO data, WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    String userId = s.getUser().getId();

    Optional<ConversationState> optionalState = this.state.get(userId);

    if (optionalState.isEmpty()) {
      this.emit("conversation:data:error", null, userId, session);
      return;
    }

    ConversationState state = optionalState.get();
    state.getRealtime().appendAudio(data.getAudio());
  }

  private void startConversation(StartConversationDTO data, WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    String userId = s.getUser().getId();

    if (this.state.hasActiveConversation(userId)) {
      this.emit("conversation:start:error", null, userId, session);
      return;
    }

    Conversation conversation =
        this.service.createConversation(data.getFromLanguage(), data.getToLanguage(), s.getUser());

    Realtime realtime = new Realtime(this.secret, this.uri);
    realtime.connect(data.getFromLanguage(), data.getToLanguage());

    this.state.store(userId, conversation, realtime, session);
    this.emit("conversation:start:success", null, userId, session);
  }
}
