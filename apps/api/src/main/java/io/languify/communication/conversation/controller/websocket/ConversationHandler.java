package io.languify.communication.conversation.controller.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.languify.communication.conversation.dto.ProcessConversationDataDTO;
import io.languify.communication.conversation.dto.StartConversationDTO;
import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.model.ConversationTranscription;
import io.languify.communication.conversation.repository.ConversationTranscriptionRepository;
import io.languify.communication.conversation.service.ConversationService;
import io.languify.communication.conversation.service.ConversationTranscriptionService;
import io.languify.identity.auth.model.Session;
import io.languify.identity.user.model.User;
import io.languify.infra.logging.Logger;
import io.languify.infra.realtime.Realtime;
import io.languify.infra.realtime.RealtimeEventHandler;
import io.languify.infra.websocket.Handler;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConversationHandler extends Handler {
  private final ConversationService service;

  private final ConversationTranscriptionRepository transcriptionRepository;
  private final ConversationTranscriptionService transcriptionService;

  private final ConversationStateManager state;
  private final ObjectMapper mapper = new ObjectMapper();

  @Value("${openai.secret}")
  private String secret;

  @Value("${openai.realtime.uri}")
  private String uri;

  public void handleSegment(String segment, JsonNode data, WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    User user = s.getUser();
    UUID userId = user != null ? user.getId() : null;

    switch (segment) {
      case "start":
        try {
          StartConversationDTO dto = this.mapper.treeToValue(data, StartConversationDTO.class);
          this.startConversation(dto, session);
        } catch (Exception e) {
          Logger.error(
              log,
              "Failed to start conversation",
              e,
              "userId",
              userId,
              "sessionId",
              session.getId());
          this.emit("conversation:start:error", null, userId, session);
        }

        break;
      case "data":
        try {
          ProcessConversationDataDTO dto =
              this.mapper.treeToValue(data, ProcessConversationDataDTO.class);
          this.processConversationData(dto, session);
        } catch (Exception e) {
          Logger.error(
              log,
              "Failed to process conversation data",
              e,
              "userId",
              userId,
              "sessionId",
              session.getId());
          this.emit("conversation:data:error", null, userId, session);
        }
        break;
      case "translate":
        try {
          this.translateConversation(session);
        } catch (Exception e) {
          Logger.error(
              log,
              "Failed to translate conversation",
              e,
              "userId",
              userId,
              "sessionId",
              session.getId());
          this.emit("conversation:translate:error", null, userId, session);
        }

        break;
      case "swap":
        this.swapLanguages(session);
        break;
      case "close":
        this.closeConversation(session);
        break;
    }
  }

  private void startConversation(StartConversationDTO data, WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    UUID userId = s.getUser().getId();

    if (this.state.hasActiveConversation(userId)) {
      this.emit("conversation:start:error", null, userId, session);
      return;
    }

    Conversation conversation =
        this.service.createConversation(data.getFromLanguage(), data.getToLanguage(), s.getUser());

    Realtime realtime =
        new Realtime(
            this.secret,
            this.uri,
            new RealtimeEventHandler() {
              private final ConversationTranscription transcription =
                  transcriptionService.createConversationTranscription(conversation);

              private boolean firstDelta = true;

              @Override
              public void onOriginalTranscription(String transcript) {
                if (transcript == null) {
                  return;
                }

                transcription.setOriginalTranscript(transcript);
                transcriptionRepository.save(transcription);
              }

              @Override
              public void onTranslatedTranscription(String transcript) {
                if (transcript == null) {
                  return;
                }

                transcription.setTranslatedTranscript(transcript);
                transcriptionRepository.save(transcription);
              }

              @Override
              public void onAudioDelta(String audioDelta) {
                if (firstDelta) {
                  emit(
                      "conversation:translate:state",
                      java.util.Map.of("state", "reproducing"),
                      userId,
                      session);
                  firstDelta = false;
                }

                emit(
                    "conversation:data:delta",
                    java.util.Map.of("audio", audioDelta),
                    userId,
                    session);
              }

              @Override
              public void onAudioDone() {
                emit("conversation:data:done", null, userId, session);
              }

              @Override
              public void onTranslationDone() {
                emit(
                    "conversation:translate:state",
                    java.util.Map.of("state", "ready"),
                    userId,
                    session);

                firstDelta = true;

                Optional<ConversationState> state = ConversationHandler.this.state.get(userId);
                state.ifPresent(ConversationState::swapLanguages);
              }
            });

    realtime.connect(data.getFromLanguage(), data.getToLanguage());

    this.state.store(
        userId, conversation, realtime, session, data.getFromLanguage(), data.getToLanguage());

    this.emit("conversation:start:success", null, userId, session);
  }

  private void processConversationData(ProcessConversationDataDTO data, WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    UUID userId = s.getUser().getId();

    Optional<ConversationState> optionalState = this.state.get(userId);

    if (optionalState.isEmpty()) {
      this.emit("conversation:data:error", null, userId, session);
      return;
    }

    ConversationState state = optionalState.get();
    state.getRealtime().appendAudio(data.getAudio());
  }

  private void translateConversation(WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    UUID userId = s.getUser().getId();

    Optional<ConversationState> optionalState = this.state.get(userId);

    if (optionalState.isEmpty()) {
      this.emit("conversation:translate:error", null, userId, session);
      return;
    }

    this.emit(
        "conversation:translate:state", java.util.Map.of("state", "loading"), userId, session);

    ConversationState state = optionalState.get();
    state.getRealtime().commitAndTranslate(state.getFromLanguage(), state.getToLanguage());
  }

  private void swapLanguages(WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    UUID userId = s.getUser().getId();

    Optional<ConversationState> optionalState = this.state.get(userId);

    if (optionalState.isEmpty()) {
      this.emit("conversation:swap:error", null, userId, session);
      return;
    }

    ConversationState state = optionalState.get();
    state.swapLanguages();

    this.emit("conversation:swap:success", null, userId, session);
  }

  private void closeConversation(WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    UUID userId = s.getUser().getId();

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
}
