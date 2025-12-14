package com.languify.communication.conversation.handler;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.languify.communication.conversation.dto.ConnectionInitializationFailedEvent;
import com.languify.communication.conversation.dto.ConnectionInitializedEvent;
import com.languify.communication.conversation.dto.ConversationInitializingEvent;
import com.languify.communication.conversation.dto.ConversationServerEvent;
import com.languify.communication.conversation.dto.ConversationUpdate;
import com.languify.communication.conversation.dto.ErrorOccurredEvent;
import com.languify.communication.conversation.dto.LanguageDetectedEvent;
import com.languify.communication.conversation.dto.ResponseCompleteEvent;
import com.languify.communication.conversation.dto.TranscriptionDeltaEvent;
import com.languify.communication.conversation.dto.TranslationDeltaEvent;
import com.languify.communication.conversation.dto.AudioChunkEvent;
import com.languify.communication.conversation.model.Conversation;
import com.languify.communication.conversation.model.ConversationContext;
import com.languify.communication.conversation.model.ConversationState;
import com.languify.communication.conversation.model.MessageSpeaker;
import com.languify.communication.conversation.model.TranslationState;
import com.languify.communication.conversation.service.ConversationManager;
import com.languify.communication.conversation.service.ConversationMessageService;
import com.languify.communication.conversation.service.ConversationService;
import com.languify.communication.conversation.util.LanguageDetector;
import com.languify.identity.auth.model.Session;
import com.languify.infra.realtime.client.RealtimeClient;
import com.languify.infra.realtime.client.RealtimeClientParams;
import com.languify.infra.realtime.config.RealtimeConfig;
import com.languify.infra.realtime.dto.AudioDeltaEvent;
import com.languify.infra.realtime.dto.AudioTranscriptionDeltaEvent;
import com.languify.infra.realtime.dto.ErrorEvent;
import com.languify.infra.realtime.dto.RealtimeServerEvent;
import com.languify.infra.realtime.dto.ResponseDoneEvent;
import com.languify.infra.realtime.dto.TextDeltaEvent;
import com.languify.infra.realtime.service.RealtimeService;
import com.languify.infra.websocket.dto.WebSocketMessage;
import com.languify.infra.websocket.handler.WebSocketHandler;
import com.languify.infra.websocket.service.WebSocketManager;

@Component
public class ConversationHandler extends WebSocketHandler {
  private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
  private final ObjectMapper mapper = new ObjectMapper();
  private static final String TARGET_LANGUAGE = "en";

  private final ConversationManager manager;

  private final ConversationService service;
  private final ConversationMessageService messageService;
  private final RealtimeService realtimeService;

  public ConversationHandler(WebSocketManager wsManager, ConversationManager manager,
      ConversationService service, ConversationMessageService messageService,
      RealtimeService realtimeService) {
    super(wsManager);
    this.manager = manager;
    this.service = service;
    this.messageService = messageService;
    this.realtimeService = realtimeService;
  }

  @Override
  protected void onConnectionEstablished(WebSocketSession ws, Session session) throws Exception {
    UUID userId = session.getUser().getId();

    try {
      ConversationContext context = manager.createContext(session);
      manager.addContext(context, userId);

      initializeConversation(userId);
      initializeRealtimeClient(userId);
    } catch (Exception e) {
      manager.removeContext(userId);
      logger.error("Something went wrong while establishing Conversation.", e);
      send(new ConnectionInitializationFailedEvent(e.getMessage()), userId);
    }
  }

  private void initializeConversation(UUID userId) throws Exception {
    ConversationContext context = manager.getContext(userId)
        .orElseThrow(() -> new Exception("Conversation Context not found."));

    Conversation conversation = service.create(context.getSession().getUser());
    manager.setContextConversation(conversation, userId);

    send(new ConversationInitializingEvent(), userId);
  }

  private void initializeRealtimeClient(UUID userId) throws Exception {
    ConversationContext context = manager.getContext(userId)
        .orElseThrow(() -> new Exception("Conversation Context not found."));

    Conversation conversation =
        context.getConversation().orElseThrow(() -> new Exception("Conversation not found."));

    RealtimeConfig realtimeConfig = RealtimeConfig.translation();

    Consumer<RealtimeServerEvent> handler = event -> {
      try {
        handleRealtimeEvent(event, userId);
      } catch (Exception e) {
        logger.error("Error handling realtime event", e);
        send(new ErrorOccurredEvent("processing_error", e.getMessage()), userId);
      }
    };

    RealtimeClientParams params = new RealtimeClientParams() {
      public void onConnect(RealtimeClient client) {}

      public void onDisconnect(RealtimeClient client) {}
    };

    RealtimeClient realtimeClient = realtimeService.createClient(realtimeConfig, handler, params);
    manager.setContextRealtimeClient(realtimeClient, userId);
    try {
      realtimeClient.connect().get(10, java.util.concurrent.TimeUnit.SECONDS);

      Conversation updated =
          service.update(new ConversationUpdate(null, ConversationState.ACTIVE, null, null),
              conversation.getId());
      manager.setContextConversation(updated, userId);
      send(new ConnectionInitializedEvent(), userId);
    } catch (Exception e) {
      logger.error("Failed to initialize realtime connection", e);
      realtimeClient.disconnect();
      manager.removeContext(userId);
      send(new ConnectionInitializationFailedEvent(), userId);
    }
  }

  private void send(ConversationServerEvent event, UUID userId) {
    Optional<WebSocketSession> optionalWs = super.manager.getSession(userId);

    try {
      if (optionalWs.isEmpty()) {
        logger.error("Trying to send message without a WebSocket Session.");
        return;
      }

      WebSocketSession ws = optionalWs.get();

      var dataNode = mapper.valueToTree(event);
      if (dataNode.isObject()) {
        dataNode = dataNode.deepCopy();
        ((com.fasterxml.jackson.databind.node.ObjectNode) dataNode).remove("type");
      }

      var payload = mapper.createObjectNode();
      payload.put("type", event.type());
      payload.set("data", dataNode);

      String data = mapper.writeValueAsString(payload);
      ws.sendMessage(new TextMessage(data));
    } catch (Exception ex) {
      logger.error("Something went wrong while sending message to WebSocket Server.", ex);
    }
  }

  protected void handleMessage(WebSocketMessage message, WebSocketSession ws, Session session) {
    UUID userId = session.getUser().getId();

    try {
      Optional<ConversationContext> optionalContext = manager.getContext(userId);
      if (optionalContext.isEmpty()) {
        logger.warn("Conversation Context not found for user {}. Dropping message {}", userId,
            message.type());
        return;
      }
      ConversationContext context = optionalContext.get();

      RealtimeClient client =
          context.getClient().orElseThrow(() -> new Exception("Realtime client not found."));

      switch (message.type()) {
        case "audio.chunk" -> {
          // Accept payloads in both {audio: "..."} and {data: {audio: "..."}} shapes
          String audio = extractAudioBase64(message.data());
          if (audio == null || audio.isEmpty()) {
            logger.warn("Received audio.chunk without audio data.");
            return;
          }

          boolean sent = client.sendAudioChunk(audio);
          if (sent) {
            context.setAudioBuffered(true);
          } else {
            logger.warn("Failed to send audio chunk to realtime client.");
          }
        }

        case "audio.commit" -> {
          if (!context.isAudioBuffered()) {
            logger
                .warn("Audio commit requested with empty buffer; ignoring to avoid OpenAI error.");
            return;
          }

          if (!client.commitAudio()) {
            logger.warn("Failed to commit audio buffer.");
            return;
          }

          if (!client.createResponse()) {
            logger.warn("Failed to create response after commit.");
            return;
          }
          context.setTranslationState(TranslationState.PROCESSING_AUDIO);
          context.setAudioBuffered(false);

          // Notify frontend to show processing state
          send(new TranscriptionDeltaEvent("", ""), userId);
        }

        case "interrupt" -> handleInterruption(context, client);

        case "close" -> handleConversationClose(context, userId);

        default -> logger.warn("Unknown message type: {}", message.type());
      }
    } catch (Exception e) {
      logger.error("Something went wrong while handling client message:", e);
    }
  }

  @SuppressWarnings("unchecked")
  private String extractAudioBase64(Object data) {
    if (data == null)
      return null;

    if (data instanceof java.util.Map<?, ?> map) {
      Object direct = map.get("audio");
      if (direct instanceof String str && !str.isEmpty())
        return str;

      Object nested = map.get("data");
      if (nested instanceof java.util.Map<?, ?> nestedMap) {
        Object nestedAudio = nestedMap.get("audio");
        if (nestedAudio instanceof String str && !str.isEmpty())
          return str;
      }
    }

    // Fallback: try to deserialize generically
    try {
      var node = mapper.valueToTree(data);
      if (node.hasNonNull("audio"))
        return node.get("audio").asText();
      if (node.has("data") && node.get("data").hasNonNull("audio"))
        return node.get("data").get("audio").asText();
    } catch (Exception ignored) {
    }

    return null;
  }

  private void handleInterruption(ConversationContext context, RealtimeClient client) {
    // Cancel current response if any
    if (context.getCurrentResponseId() != null) {
      client.cancelResponse(context.getCurrentResponseId());
    }

    // Clear input buffer
    client.clearInputAudioBuffer();

    // Reset state
    context.setCurrentTranscription(new StringBuilder());
    context.setCurrentTranslation(new StringBuilder());
    context.setCurrentResponseId(null);
    context.setAudioBuffered(false);

    String sourceLanguage =
        context.getConversation().map(Conversation::getSourceLanguage).orElse(null);
    TranslationState nextState = sourceLanguage == null ? TranslationState.AWAITING_SOURCE_LANGUAGE
        : TranslationState.ACTIVE_TRANSLATION;
    context.setTranslationState(nextState);
  }

  private void handleConversationClose(ConversationContext context, UUID userId) throws Exception {
    Conversation conversation =
        context.getConversation().orElseThrow(() -> new Exception("Conversation not found."));

    // Update conversation state
    service.update(new ConversationUpdate(null, ConversationState.COMPLETED, null, null),
        conversation.getId());

    // Close realtime client
    context.getClient().ifPresent(RealtimeClient::disconnect);

    // Remove context
    manager.removeContext(userId);
  }

  @Override
  protected void onConnectionClosed(WebSocketSession ws, Session session, CloseStatus status) {
    UUID userId = session.getUser().getId();
    try {
      ConversationContext context = manager.getContext(userId).orElse(null);
      if (context != null) {
        handleConversationClose(context, userId);
      } else {
        manager.removeContext(userId);
      }
    } catch (Exception e) {
      logger.error("Error while closing conversation on WS close", e);
      manager.removeContext(userId);
    }
  }

  private void handleRealtimeEvent(RealtimeServerEvent event, UUID userId) throws Exception {
    ConversationContext context = manager.getContext(userId)
        .orElseThrow(() -> new Exception("Conversation Context not found."));

    Conversation conversation =
        context.getConversation().orElseThrow(() -> new Exception("Conversation not found."));

    RealtimeClient client =
        context.getClient().orElseThrow(() -> new Exception("Realtime client not found."));

    switch (event) {
      case AudioTranscriptionDeltaEvent transcriptionEvent -> handleTranscription(
          transcriptionEvent, context, conversation, client, userId);

      case TextDeltaEvent textEvent -> handleTranslation(textEvent, context, userId);

      case AudioDeltaEvent audioEvent -> handleAudioChunk(audioEvent, userId);

      case ResponseDoneEvent doneEvent -> handleResponseComplete(doneEvent, context, conversation,
          client, userId);

      case ErrorEvent errorEvent -> handleError(errorEvent, userId);

      default -> logger.debug("Unhandled event type: {}", event.type());
    }
  }

  private void handleTranscription(AudioTranscriptionDeltaEvent event, ConversationContext context,
      Conversation conversation, RealtimeClient client, UUID userId) throws Exception {
    // Accumulate transcription
    context.getCurrentTranscription().append(event.delta());
    String fullTranscription = context.getCurrentTranscription().toString();

    // Send delta to mobile
    send(new TranscriptionDeltaEvent(event.delta(), fullTranscription), userId);

    conversation = maybeDetectLanguage(fullTranscription, context, conversation, client, userId,
        false);

    // Store response ID for potential cancellation
    if (event.responseId() != null) {
      context.setCurrentResponseId(event.responseId());
    }
  }

  private void handleTranslation(TextDeltaEvent event, ConversationContext context, UUID userId) {
    // Accumulate translation
    context.getCurrentTranslation().append(event.delta());
    String fullTranslation = context.getCurrentTranslation().toString();

    // Send delta to mobile
    send(new TranslationDeltaEvent(event.delta(), fullTranslation), userId);
  }

  private void handleAudioChunk(AudioDeltaEvent event, UUID userId) {
    // Forward audio chunk to mobile for playback
    send(new AudioChunkEvent(event.delta()), userId);
  }

  private void handleResponseComplete(ResponseDoneEvent event, ConversationContext context,
      Conversation conversation, RealtimeClient client, UUID userId) {
    String transcription = context.getCurrentTranscription().toString().trim();
    String translation = context.getCurrentTranslation().toString().trim();

    try {
      conversation =
          maybeDetectLanguage(transcription, context, conversation, client, userId, true);
    } catch (Exception e) {
      logger.error("Failed to detect language before persisting message.", e);
      send(new ErrorOccurredEvent("language_detection_failed", "Could not detect language"),
          userId);
    }

    if (conversation.getSourceLanguage() == null || conversation.getTargetLanguage() == null) {
      logger.warn("Languages are not set for conversation {}, skipping message persistence.",
          conversation.getId());
    } else if (!transcription.isEmpty()) {
      messageService.create(conversation, transcription,
          translation.isEmpty() ? transcription : translation, conversation.getSourceLanguage(),
          conversation.getTargetLanguage(), MessageSpeaker.AI,
          event.responseId() == null ? context.getCurrentResponseId() : event.responseId());
    }

    context.setCurrentTranscription(new StringBuilder());
    context.setCurrentTranslation(new StringBuilder());
    context.setCurrentResponseId(null);
    context.setTranslationState(TranslationState.ACTIVE_TRANSLATION);
    context.setAudioBuffered(false);

    send(new ResponseCompleteEvent(event.responseId()), userId);
  }

  private void handleError(ErrorEvent event, UUID userId) {
    logger.error("OpenAI error: {}", event);
    send(new ErrorOccurredEvent("openai_error", "An error occurred"), userId);
  }

  private Conversation maybeDetectLanguage(String transcription, ConversationContext context,
      Conversation conversation, RealtimeClient client, UUID userId, boolean forceDetection)
      throws Exception {
    if (conversation.getSourceLanguage() != null)
      return conversation;

    String normalized = transcription == null ? "" : transcription.trim();
    if (normalized.isEmpty())
      return conversation;

    if (!forceDetection && !hasMinimumSignalForDetection(normalized))
      return conversation;

    String detectedLanguage = LanguageDetector.detectLanguage(normalized);

    conversation = service.update(
        new ConversationUpdate(null, null, detectedLanguage, TARGET_LANGUAGE),
        conversation.getId());
    manager.setContextConversation(conversation, userId);

    client.updateSession(buildTranslationConfig(detectedLanguage));

    context.setTranslationState(TranslationState.ACTIVE_TRANSLATION);
    send(new LanguageDetectedEvent(detectedLanguage), userId);

    return conversation;
  }

  private boolean hasMinimumSignalForDetection(String text) {
    if (text.length() >= 12)
      return true;

    String[] tokens = text.trim().split("\\s+");
    return tokens.length >= 3;
  }

  private RealtimeConfig buildTranslationConfig(String sourceLanguage) {
    String instruction = String.format(
        "You are a realtime translator. When the user speaks %s, provide an English translation "
            + "with concise, natural phrasing and speak it back in English. When the user speaks English, "
            + "translate it to %s and return speech audio in %s. Keep responses short.",
        sourceLanguage, sourceLanguage, sourceLanguage);

    return new RealtimeConfig(List.of("text", "audio"), instruction, "alloy", "pcm16", "pcm16",
        new RealtimeConfig.InputAudioTranscription("whisper-1"),
        new RealtimeConfig.TurnDetection("server_vad"), 0.8);
  }
}
