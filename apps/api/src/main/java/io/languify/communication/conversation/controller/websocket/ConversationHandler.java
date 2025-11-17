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

    Logger.info(
        log,
        "Handling conversation segment",
        "userId",
        userId,
        "sessionId",
        session.getId(),
        "segment",
        segment);

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
              session.getId(),
              "segment",
              segment,
              "fromLanguage",
              data.has("fromLanguage") ? data.get("fromLanguage").asText() : null,
              "toLanguage",
              data.has("toLanguage") ? data.get("toLanguage").asText() : null);

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
              session.getId(),
              "segment",
              segment,
              "hasAudioData",
              data.has("audio"));
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
              session.getId(),
              "segment",
              segment);

          this.emit("conversation:translate:error", null, userId, session);
        }

        break;
      case "swap":
        try {
          this.swapLanguages(session);
        } catch (Exception e) {
          Logger.error(
              log,
              "Failed to swap conversation languages",
              e,
              "userId",
              userId,
              "sessionId",
              session.getId(),
              "segment",
              segment);
          this.emit("conversation:swap:error", null, userId, session);
        }
        
        break;
      case "close":
        try {
          this.closeConversation(session);
        } catch (Exception e) {
          Logger.error(
              log,
              "Failed to close conversation",
              e,
              "userId",
              userId,
              "sessionId",
              session.getId(),
              "segment",
              segment);
          this.emit("conversation:close:error", null, userId, session);
        }
        break;
      default:
        Logger.warn(
            log,
            "Unknown conversation segment received",
            "userId",
            userId,
            "sessionId",
            session.getId(),
            "segment",
            segment);
        break;
    }
  }

  private void startConversation(StartConversationDTO data, WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    UUID userId = s.getUser().getId();

    Logger.info(
        log,
        "Starting conversation",
        "userId",
        userId,
        "sessionId",
        session.getId(),
        "fromLanguage",
        data.getFromLanguage(),
        "toLanguage",
        data.getToLanguage());

    if (this.state.hasActiveConversation(userId)) {
      Logger.warn(
          log,
          "User already has active conversation",
          "userId",
          userId,
          "sessionId",
          session.getId());
      
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
                if (transcript == null) return;

                transcription.setOriginalTranscript(transcript);
                transcriptionRepository.save(transcription);

                Logger.info(
                    log,
                    "Original transcription saved",
                    "userId",
                    userId,
                    "conversationId",
                    conversation.getId(),
                    "transcriptLength",
                    transcript.length());
              }

              @Override
              public void onTranslatedTranscription(String transcript) {
                if (transcript == null) {
                  return;
                }

                transcription.setTranslatedTranscript(transcript);
                transcriptionRepository.save(transcription);

                Logger.info(
                    log,
                    "Translated transcription saved",
                    "userId",
                    userId,
                    "conversationId",
                    conversation.getId(),
                    "transcriptLength",
                    transcript.length());
              }

              @Override
              public void onAudioDelta(String audioDelta) {
                if (firstDelta) {
                  Logger.info(
                      log,
                      "Audio streaming started",
                      "userId",
                      userId,
                      "conversationId",
                      conversation.getId());

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
                Logger.info(
                    log,
                    "Audio streaming completed",
                    "userId",
                    userId,
                    "conversationId",
                    conversation.getId());

                emit("conversation:data:done", null, userId, session);
              }

              @Override
              public void onTranslationDone() {
                Logger.info(
                    log,
                    "Translation completed",
                    "userId",
                    userId,
                    "conversationId",
                    conversation.getId());

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

    Logger.info(
        log,
        "Conversation started successfully",
        "userId",
        userId,
        "sessionId",
        session.getId(),
        "conversationId",
        conversation.getId());

    this.emit("conversation:start:success", null, userId, session);
  }

  private void processConversationData(ProcessConversationDataDTO data, WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    UUID userId = s.getUser().getId();

    Optional<ConversationState> optionalState = this.state.get(userId);

    if (optionalState.isEmpty()) {
      Logger.warn(
          log,
          "No active conversation found for data processing",
          "userId",
          userId,
          "sessionId",
          session.getId());

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
      Logger.warn(
          log,
          "No active conversation found for translation",
          "userId",
          userId,
          "sessionId",
          session.getId());

      this.emit("conversation:translate:error", null, userId, session);
      return;
    }

    ConversationState state = optionalState.get();

    Logger.info(
        log,
        "Translating conversation",
        "userId",
        userId,
        "sessionId",
        session.getId(),
        "conversationId",
        state.getConversation().getId(),
        "fromLanguage",
        state.getFromLanguage(),
        "toLanguage",
        state.getToLanguage());

    this.emit(
        "conversation:translate:state", java.util.Map.of("state", "loading"), userId, session);

    state.getRealtime().commitAndTranslate(state.getFromLanguage(), state.getToLanguage());
  }

  private void swapLanguages(WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    UUID userId = s.getUser().getId();

    Optional<ConversationState> optionalState = this.state.get(userId);

    if (optionalState.isEmpty()) {
      Logger.warn(
          log,
          "No active conversation found for language swap",
          "userId",
          userId,
          "sessionId",
          session.getId());

      this.emit("conversation:swap:error", null, userId, session);
      return;
    }

    ConversationState state = optionalState.get();

    Logger.info(
        log,
        "Swapping conversation languages",
        "userId",
        userId,
        "sessionId",
        session.getId(),
        "conversationId",
        state.getConversation().getId(),
        "fromLanguage",
        state.getFromLanguage(),
        "toLanguage",
        state.getToLanguage());

    state.swapLanguages();

    Logger.info(
        log,
        "Languages swapped successfully",
        "userId",
        userId,
        "sessionId",
        session.getId(),
        "conversationId",
        state.getConversation().getId(),
        "newFromLanguage",
        state.getFromLanguage(),
        "newToLanguage",
        state.getToLanguage()
    );

    this.emit("conversation:swap:success", null, userId, session);
  }

  private void closeConversation(WebSocketSession session) {
    Session s = this.extractSessionFromWebSocketSession(session);
    UUID userId = s.getUser().getId();

    Optional<ConversationState> optionalState = this.state.get(userId);

    if (optionalState.isEmpty()) {
      Logger.warn(
          log,
          "No active conversation found to close",
          "userId",
          userId,
          "sessionId",
          session.getId()
      );

      this.emit("conversation:close:error", null, userId, session);
      return;
    }

    ConversationState state = optionalState.get();

    Logger.info(
        log,
        "Closing conversation",
        "userId",
        userId,
        "sessionId",
        session.getId(),
        "conversationId",
        state.getConversation().getId()
    );

    state.getRealtime().disconnect();
    this.state.remove(userId);

    Logger.info(
        log,
        "Conversation closed successfully",
        "userId",
        userId,
        "sessionId",
        session.getId()
    );

    this.emit("conversation:close:success", null, userId, session);
  }
}
