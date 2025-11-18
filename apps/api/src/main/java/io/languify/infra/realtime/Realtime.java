package io.languify.infra.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.languify.infra.logging.Logger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Realtime {
  private static final int SAMPLE_RATE = 16000;
  private static final int BYTES_PER_SAMPLE = 2; // PCM16
  private static final long MIN_AUDIO_BYTES = (long) (SAMPLE_RATE * BYTES_PER_SAMPLE * 0.1); // 100ms

  private final String REALTIME_SECRET;
  private final String REALTIME_URI;

  private final RealtimeEventHandler handler;
  private final ObjectMapper mapper = new ObjectMapper();
  private final StringBuilder messageBuffer = new StringBuilder();

  private int audioChunksAppended = 0;
  private long totalPcmBytes = 0;
  private boolean awaitingCommitAck = false;
  private String pendingResponseInstructions = null;

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
            .header("Authorization", "Bearer " + this.REALTIME_SECRET)
            .header("OpenAI-Beta", "realtime=v1")
            .subprotocols("realtime");

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
                    try {
                      messageBuffer.append(data);

                      if (last) {
                        String completeMessage = messageBuffer.toString();
                        messageBuffer.setLength(0);

                        JsonNode event = mapper.readTree(completeMessage);
                        handleEvent(event);
                      }
                    } catch (Exception e) {
                      Logger.error(log, "Error parsing OpenAI event", e);
                      messageBuffer.setLength(0);
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
        awaitingCommitAck = false;
        pendingResponseInstructions = null;
        break;

      case "input_audio_buffer.committed":
        Logger.info(
            log,
            "OpenAI confirmed audio commit",
            "pendingResponse",
            pendingResponseInstructions != null);

        awaitingCommitAck = false;

        if (pendingResponseInstructions != null) {
          String instructions = pendingResponseInstructions;
          pendingResponseInstructions = null;
          sendResponseWithInstructions(instructions);
        }
        break;

      case "input_audio_buffer.cleared":
        Logger.info(log, "OpenAI reported audio buffer cleared");
        break;

      case "session.created":
      case "session.updated":
      case "input_audio_buffer.speech_started":
      case "input_audio_buffer.speech_stopped":
      case "conversation.item.created":
      case "conversation.item.added":
      case "response.created":
      case "response.output_item.added":
      case "response.content_part.added":
      case "response.text.delta":
      case "response.text.done":
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

    session.put("modalities", new String[] {"text", "audio"});
    session.put("instructions", instructions);
    session.put("input_audio_format", "pcm16");
    session.put("output_audio_format", "pcm16");
    session.put("input_audio_transcription", Map.of("model", "whisper-1"));
    session.put("turn_detection", null);

    return Map.of("type", "session.update", "session", session);
  }

  public void appendAudio(String base64Audio) {
    try {
      byte[] pcmBytes = Base64.getDecoder().decode(base64Audio);
      totalPcmBytes += pcmBytes.length;

      double audioMs = (pcmBytes.length * 1000.0) / (SAMPLE_RATE * BYTES_PER_SAMPLE);

      Logger.info(
          log,
          "Appending audio to buffer",
          "pcmBytes",
          pcmBytes.length,
          "totalPcmBytes",
          totalPcmBytes,
          "audioMs",
          String.format("%.2f", audioMs),
          "totalMs",
          String.format("%.2f", (totalPcmBytes * 1000.0) / (SAMPLE_RATE * BYTES_PER_SAMPLE)));

      Map<String, Object> event = Map.of("type", "input_audio_buffer.append", "audio", base64Audio);
      String eventJson = mapper.writeValueAsString(event);

      send(eventJson);
      audioChunksAppended++;
    } catch (Exception e) {
      Logger.error(log, "Error appending audio", e);
    }
  }


  public void commitAndTranslate(String fromLanguage, String toLanguage) {
    double totalMs = (totalPcmBytes * 1000.0) / (SAMPLE_RATE * BYTES_PER_SAMPLE);

    if (totalPcmBytes < MIN_AUDIO_BYTES) {
      Logger.warn(
          log,
          "Insufficient audio for commit, skipping translation",
          "fromLanguage",
          fromLanguage,
          "toLanguage",
          toLanguage,
          "totalPcmBytes",
          totalPcmBytes,
          "totalMs",
          String.format("%.2f", totalMs),
          "minRequiredMs",
          100.0);

      return;
    }

    if (awaitingCommitAck) {
      Logger.warn(
          log,
          "Commit already in progress, ignoring new request",
          "fromLanguage",
          fromLanguage,
          "toLanguage",
          toLanguage);
      return;
    }

    Logger.info(
        log,
        "Committing audio buffer and requesting translation",
        "fromLanguage",
        fromLanguage,
        "toLanguage",
        toLanguage,
        "chunksAppended",
        audioChunksAppended,
        "totalPcmBytes",
        totalPcmBytes,
        "totalMs",
        String.format("%.2f", totalMs));

    try {
      Logger.info(log, "Sending input_audio_buffer.commit to OpenAI");

      Map<String, Object> commitEvent = Map.of("type", "input_audio_buffer.commit");
      send(mapper.writeValueAsString(commitEvent));

      String instructions =
          String.format(
              "Translate this speech from %s to %s. Only provide the translation.",
              fromLanguage, toLanguage);

      awaitingCommitAck = true;
      pendingResponseInstructions = instructions;
    } catch (JsonProcessingException e) {
      Logger.error(log, "Error committing translation", e);
    }
  }

  public void clearBuffer() {
    try {
      Logger.info(
          log,
          "Clearing audio buffer",
          "chunksCleared",
          audioChunksAppended,
          "bytesCleared",
          totalPcmBytes);

      Map<String, Object> clearEvent = Map.of("type", "input_audio_buffer.clear");
      send(mapper.writeValueAsString(clearEvent));

      audioChunksAppended = 0;
      totalPcmBytes = 0;
    } catch (JsonProcessingException e) {
      Logger.error(log, "Error clearing buffer", e);
    }
  }

  public void disconnect() {
    Logger.info(log, "Disconnecting from OpenAI Realtime API");
    this.socket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing");
  }

  private void sendResponseWithInstructions(String instructions) {
    try {
      Map<String, Object> response = Map.of("instructions", instructions);
      Map<String, Object> responseEvent = Map.of("type", "response.create", "response", response);

      send(mapper.writeValueAsString(responseEvent));

      Logger.info(log, "Translation request sent to OpenAI");
    } catch (JsonProcessingException e) {
      Logger.error(log, "Error sending translation response request", e);
    }
  }
}
