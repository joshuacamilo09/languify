package com.languify.infra.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                      log.error("Error parsing OpenAI event", e);
                      messageBuffer.setLength(0);
                    }

                    return WebSocket.Listener.super.onText(socket, data, last);
                  }

                  @Override
                  public CompletionStage<?> onClose(WebSocket socket, int code, String reason) {

                    return WebSocket.Listener.super.onClose(socket, code, reason);
                  }

                  @Override
                  public void onError(WebSocket socket, Throwable error) {
                    log.error(
                        "OpenAI Realtime connection error | errorType={}",
                        error.getClass().getSimpleName(),
                        error);

                    WebSocket.Listener.super.onError(socket, error);
                  }
                })
            .join();
  }

  private void handleEvent(JsonNode event) {
    if (event == null || !event.has("type")) {
      return;
    }

    String type = event.get("type").asText();

    switch (type) {
      case "conversation.item.input_audio_transcription.completed":
        if (event.has("transcript")) {
          String transcript = event.get("transcript").asText();
          this.handler.onOriginalTranscription(transcript);
        }

        break;

      case "response.audio_transcript.done":
        if (event.has("transcript")) {
          String transcript = event.get("transcript").asText();
          this.handler.onTranslatedTranscription(transcript);
        }

        break;

      case "response.audio.delta":
        if (event.has("delta")) this.handler.onAudioDelta(event.get("delta").asText());
        break;

      case "response.audio.done":
        this.handler.onAudioDone();
        break;

      case "response.done":
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

        log.error(
            "OpenAI Realtime API error | errorType={} errorCode={} eventId={} message={}",
            errorType,
            errorCode,
            event.has("event_id") ? event.get("event_id").asText() : "unknown",
            errorMessage);
        awaitingCommitAck = false;
        pendingResponseInstructions = null;
        break;

      case "input_audio_buffer.committed":

        awaitingCommitAck = false;

        if (pendingResponseInstructions != null) {
          String instructions = pendingResponseInstructions;
          pendingResponseInstructions = null;
          sendResponseWithInstructions(instructions);
        }
        break;

      case "input_audio_buffer.cleared":
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
        break;
    }
  }

  public void send(String message) {
    this.socket.sendText(message, true);
  }

  private void configureSession(WebSocket socket, String fromLanguage, String toLanguage) {

    try {
      String instructions =
          String.format(
              "You are a real-time translator. Translate spoken audio from %s to %s. Maintain the speaker's tone and emotion. Only provide the translation, do not add extra commentary.",
              fromLanguage, toLanguage);

      Map<String, Object> config = getSessionConfiguration(instructions);
      socket.sendText(mapper.writeValueAsString(config), true);

    } catch (JsonProcessingException e) {
      log.error("Error creating session config", e);
    }
  }

  private Map<String, Object> getSessionConfiguration(String instructions) {
    java.util.HashMap<String, Object> session = new java.util.HashMap<>();

    session.put("modalities", new String[] {"audio", "text"});
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


      Map<String, Object> event = Map.of("type", "input_audio_buffer.append", "audio", base64Audio);
      String eventJson = mapper.writeValueAsString(event);

      send(eventJson);
      audioChunksAppended++;
    } catch (Exception e) {
      log.error("Error appending audio", e);
    }
  }


  public void commitAndTranslate(String fromLanguage, String toLanguage) {
    double totalMs = (totalPcmBytes * 1000.0) / (SAMPLE_RATE * BYTES_PER_SAMPLE);

    if (totalPcmBytes < MIN_AUDIO_BYTES) {

      return;
    }

    if (awaitingCommitAck) {
      return;
    }


    try {

      Map<String, Object> commitEvent = Map.of("type", "input_audio_buffer.commit");
      send(mapper.writeValueAsString(commitEvent));

      String instructions =
          String.format(
              "Translate this speech from %s to %s. Only provide the translation.",
              fromLanguage, toLanguage);

      awaitingCommitAck = true;
      pendingResponseInstructions = instructions;
    } catch (JsonProcessingException e) {
      log.error("Error committing translation", e);
    }
  }

  public void clearBuffer() {
    try {

      Map<String, Object> clearEvent = Map.of("type", "input_audio_buffer.clear");
      send(mapper.writeValueAsString(clearEvent));

      audioChunksAppended = 0;
      totalPcmBytes = 0;
    } catch (JsonProcessingException e) {
      log.error("Error clearing buffer", e);
    }
  }

  public void disconnect() {
    this.socket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing");
  }

  private void sendResponseWithInstructions(String instructions) {
    try {
      Map<String, Object> response = Map.of(
          "modalities", new String[] {"audio", "text"},
          "instructions", instructions
      );
      Map<String, Object> responseEvent = Map.of("type", "response.create", "response", response);

      send(mapper.writeValueAsString(responseEvent));

    } catch (JsonProcessingException e) {
      log.error("Error sending translation response request", e);
    }
  }
}
