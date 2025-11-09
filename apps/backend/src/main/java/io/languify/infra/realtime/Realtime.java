package io.languify.infra.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class Realtime {
  private final String REALTIME_SECRET;
  private final String REALTIME_URI;
  private final ObjectMapper mapper = new ObjectMapper();

  private WebSocket socket;

  public Realtime(String secret, String uri) {
    this.REALTIME_SECRET = secret;
    this.REALTIME_URI = uri;
  }

  public void connect(String sourceLanguage, String targetLanguage) {
    HttpClient client = HttpClient.newHttpClient();

    WebSocket.Builder builder =
        client
            .newWebSocketBuilder()
            .header("Authorization", "Bearer " + this.REALTIME_SECRET)
            .header("OpenAI-Beta", "realtime=v1");

    this.socket =
        builder
            .buildAsync(
                URI.create(this.REALTIME_URI),
                new WebSocket.Listener() {
                  @Override
                  public void onOpen(WebSocket socket) {
                    System.out.println("Connected to OpenAI Realtime API");
                    WebSocket.Listener.super.onOpen(socket);
                    configureSession(sourceLanguage, targetLanguage);
                  }

                  @Override
                  public CompletionStage<?> onText(
                      WebSocket socket, CharSequence data, boolean last) {
                    System.out.println("Received: " + data);
                    return WebSocket.Listener.super.onText(socket, data, last);
                  }

                  @Override
                  public CompletionStage<?> onClose(WebSocket socket, int code, String reason) {
                    System.out.println("Closed: " + reason);
                    return WebSocket.Listener.super.onClose(socket, code, reason);
                  }

                  @Override
                  public void onError(WebSocket socket, Throwable error) {
                    System.err.println("Error: " + error.getMessage());
                    WebSocket.Listener.super.onError(socket, error);
                  }
                })
            .join();
  }

  public void send(String message) {
    this.socket.sendText(message, true);
  }

  private Map<String, Object> getSessionConfiguration(
      String instructions, Map<String, Object> transcription) {
    Map<String, Object> session =
        Map.of(
            "modalities", new String[] {"text", "audio"},
            "instructions", instructions,
            "voice", "alloy",
            "input_audio_transcription", transcription,
            "turn_detection", null,
            "temperature", 0.8);

    return Map.of("type", "session.update", "session", session);
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
      System.err.println("Error creating session config: " + e.getMessage());
    }
  }

  public void appendAudio(String base64Audio) {
    try {
      Map<String, Object> event = Map.of("type", "input_audio_buffer.append", "audio", base64Audio);
      send(mapper.writeValueAsString(event));
    } catch (JsonProcessingException e) {
      System.err.println("Error appending audio: " + e.getMessage());
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
      System.err.println("Error committing translation: " + e.getMessage());
    }
  }

  public void clearBuffer() {
    try {
      Map<String, Object> clearEvent = Map.of("type", "input_audio_buffer.clear");
      send(mapper.writeValueAsString(clearEvent));
    } catch (JsonProcessingException e) {
      System.err.println("Error clearing buffer: " + e.getMessage());
    }
  }

  public void disconnect() {
    this.socket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing");
  }
}
