package io.languify.infra.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.languify.infra.logging.Logger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Realtime {
  private final String REALTIME_SECRET;
  private final String REALTIME_URI;

  private final RealtimeEventHandler handler;
  private final ObjectMapper mapper = new ObjectMapper();

  private WebSocket socket;

  public Realtime(String secret, String uri, RealtimeEventHandler handler) {
    this.REALTIME_SECRET = secret;
    this.REALTIME_URI = uri;

    this.handler = handler;
  }

  public void connect(String sourceLanguage, String targetLanguage) {
    String uriWithModel = this.REALTIME_URI + "?model=gpt-4o-realtime-preview-2024-10-01";

    Logger.info(
        log,
        "Connecting to OpenAI Realtime API",
        "sourceLanguage",
        sourceLanguage,
        "targetLanguage",
        targetLanguage,
        "uri",
        uriWithModel);

    HttpClient client = HttpClient.newHttpClient();

    WebSocket.Builder builder =
        client
            .newWebSocketBuilder()
            .header("Authorization", "Bearer " + this.REALTIME_SECRET);

    this.socket =
        builder
            .buildAsync(
                URI.create(uriWithModel),
                new WebSocket.Listener() {
                  @Override
                  public void onOpen(WebSocket socket) {
                    Logger.info(
                        log,
                        "OpenAI Realtime connection established",
                        "sourceLanguage",
                        sourceLanguage,
                        "targetLanguage",
                        targetLanguage);

                    WebSocket.Listener.super.onOpen(socket);
                    configureSession(socket, sourceLanguage, targetLanguage);
                  }

                  @Override
                  public CompletionStage<?> onText(
                      WebSocket socket, CharSequence data, boolean last) {
                    Logger.info(log, "Realtime payload received", "payload", data.toString());
                    try {
                      JsonNode event = mapper.readTree(data.toString());
                      handleEvent(event);
                    } catch (Exception e) {
                      Logger.error(log, "Error parsing OpenAI event", e);
                    }

                    return WebSocket.Listener.super.onText(socket, data, last);
                  }

                  @Override
                  public CompletionStage<?> onClose(WebSocket socket, int code, String reason) {
                    Logger.info(
                        log,
                        "OpenAI Realtime connection closed",
                        "code",
                        code,
                        "reason",
                        reason != null ? reason : "No reason provided");

                    return WebSocket.Listener.super.onClose(socket, code, reason);
                  }

                  @Override
                  public void onError(WebSocket socket, Throwable error) {
                    Logger.error(
                        log,
                        "OpenAI Realtime connection error",
                        error,
                        "errorType",
                        error.getClass().getSimpleName());

                    WebSocket.Listener.super.onError(socket, error);
                  }
                })
            .join();
  }

  private void handleEvent(JsonNode event) {
    if (event == null || !event.has("type")) {
      Logger.warn(log, "Received event without type field");
      return;
    }

    String type = event.get("type").asText();

    Logger.info(log, "Handling OpenAI event", "eventType", type);

    switch (type) {
      case "conversation.item.input_audio_transcription.completed":
        if (event.has("transcript")) {
          String transcript = event.get("transcript").asText();
          Logger.info(
              log,
              "Original transcription completed",
              "transcriptLength",
              transcript.length());
          this.handler.onOriginalTranscription(transcript);
        }

        break;

      case "response.audio_transcript.done":
        if (event.has("transcript")) {
          String transcript = event.get("transcript").asText();
          Logger.info(
              log,
              "Translated transcription completed",
              "transcriptLength",
              transcript.length());

          this.handler.onTranslatedTranscription(transcript);
        }

        break;

      case "response.audio.delta":
        if (event.has("delta")) this.handler.onAudioDelta(event.get("delta").asText());

        break;

      case "response.audio.done":
        Logger.info(log, "Audio response completed");
        this.handler.onAudioDone();

        break;

      case "response.done":
        Logger.info(log, "Translation response completed");
        this.handler.onTranslationDone();

        break;

      case "error":
        String errorType = event.has("error") && event.get("error").has("type")
            ? event.get("error").get("type").asText()
            : "unknown";
        String errorCode = event.has("error") && event.get("error").has("code")
            ? event.get("error").get("code").asText()
            : "unknown";
        String errorMessage = event.has("error") && event.get("error").has("message")
            ? event.get("error").get("message").asText()
            : "No error message";

        Logger.error(
            log,
            "OpenAI Realtime API error",
            new RuntimeException(errorMessage),
            "errorType",
            errorType,
            "errorCode",
            errorCode,
            "eventId",
            event.has("event_id") ? event.get("event_id").asText() : "unknown");
        break;

      case "session.created":
      case "session.updated":
      case "input_audio_buffer.committed":
      case "input_audio_buffer.cleared":
      case "conversation.item.created":
        break;

      default:
        Logger.warn(log, "Unhandled OpenAI event type", "eventType", type);
        break;
    }
  }

  public void send(String message) {
    this.socket.sendText(message, true);
  }

  private void configureSession(WebSocket socket, String sourceLanguage, String targetLanguage) {
    Logger.info(
        log,
        "Configuring OpenAI Realtime session",
        "sourceLanguage",
        sourceLanguage,
        "targetLanguage",
        targetLanguage);

    try {
      String instructions =
          String.format(
              "You are a real-time translator. Translate spoken audio from %s to %s. Maintain the speaker's tone and emotion. Only provide the translation, do not add extra commentary.",
              sourceLanguage, targetLanguage);

      Map<String, Object> config = getSessionConfiguration(instructions);
      socket.sendText(mapper.writeValueAsString(config), true);

      Logger.info(log, "OpenAI Realtime session configured successfully");
    } catch (JsonProcessingException e) {
      Logger.error(log, "Error creating session config", e);
    }
  }

  private Map<String, Object> getSessionConfiguration(String instructions) {
    java.util.HashMap<String, Object> session = new java.util.HashMap<>();
    session.put("type", "realtime");
    session.put("instructions", instructions);

    return Map.of("type", "session.update", "session", session);
  }

  public void appendAudio(String base64Audio) {
    try {
      Map<String, Object> event = Map.of("type", "input_audio_buffer.append", "audio", base64Audio);
      send(mapper.writeValueAsString(event));
    } catch (JsonProcessingException e) {
      Logger.error(log, "Error appending audio", e);
    }
  }

  public void commitAndTranslate(String fromLanguage, String toLanguage) {
    Logger.info(
        log,
        "Committing audio buffer and requesting translation",
        "fromLanguage",
        fromLanguage,
        "toLanguage",
        toLanguage);

    try {
      Map<String, Object> commitEvent = Map.of("type", "input_audio_buffer.commit");
      send(mapper.writeValueAsString(commitEvent));

      String instructions =
          String.format(
              "Translate this speech from %s to %s. Only provide the translation.",
              fromLanguage, toLanguage);

      Map<String, Object> response =
          Map.of("modalities", new String[] {"text", "audio"}, "instructions", instructions);

      Map<String, Object> responseEvent = Map.of("type", "response.create", "response", response);
      send(mapper.writeValueAsString(responseEvent));

      clearBuffer();

      Logger.info(log, "Translation request sent to OpenAI");
    } catch (JsonProcessingException e) {
      Logger.error(log, "Error committing translation", e);
    }
  }

  public void clearBuffer() {
    try {
      Map<String, Object> clearEvent = Map.of("type", "input_audio_buffer.clear");
      send(mapper.writeValueAsString(clearEvent));
    } catch (JsonProcessingException e) {
      Logger.error(log, "Error clearing buffer", e);
    }
  }

  public void disconnect() {
    Logger.info(log, "Disconnecting from OpenAI Realtime API");
    this.socket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing");
  }
}
