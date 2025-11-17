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
    HttpClient client = HttpClient.newHttpClient();

    WebSocket.Builder builder =
        client
            .newWebSocketBuilder()
            .header("Authorization", "Bearer " + this.REALTIME_SECRET);

    this.socket =
        builder
            .buildAsync(
                URI.create(this.REALTIME_URI),
                new WebSocket.Listener() {
                  @Override
                  public void onOpen(WebSocket socket) {
                    WebSocket.Listener.super.onOpen(socket);
                    configureSession(sourceLanguage, targetLanguage);
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
                    return WebSocket.Listener.super.onClose(socket, code, reason);
                  }

                  @Override
                  public void onError(WebSocket socket, Throwable error) {
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
          this.handler.onOriginalTranscription(event.get("transcript").asText());
        }

        break;

      case "response.audio_transcript.done":
        if (event.has("transcript")) {
          this.handler.onTranslatedTranscription(event.get("transcript").asText());
        }

        break;

      case "response.audio.delta":
        if (event.has("delta")) {
          this.handler.onAudioDelta(event.get("delta").asText());
        }

        break;

      case "response.audio.done":
        this.handler.onAudioDone();
        break;

      case "response.done":
        this.handler.onTranslationDone();
        break;
    }
  }

  public void send(String message) {
    this.socket.sendText(message, true);
  }

  private void configureSession(String sourceLanguage, String targetLanguage) {
    try {
      Map<String, Object> transcription = Map.of("model", "whisper-1");

      String instructions =
          String.format(
              "You are a real-time translator. Translate spoken audio from %s to %s. Maintain the speaker's tone and emotion. Only provide the translation, do not add extra commentary.",
              sourceLanguage, targetLanguage);

      Map<String, Object> config = getSessionConfiguration(instructions, transcription);
      send(mapper.writeValueAsString(config));
    } catch (JsonProcessingException e) {
      Logger.error(log, "Error creating session config", e);
    }
  }

  private Map<String, Object> getSessionConfiguration(
      String instructions, Map<String, Object> transcription) {
    Map<String, Object> session =
        Map.of(
            "modalities",
            new String[] {"text", "audio"},
            "instructions",
            instructions,
            "voice",
            "alloy",
            "input_audio_transcription",
            transcription,
            "turn_detection",
            null,
            "temperature",
            0.8);

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
    this.socket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing");
  }
}
