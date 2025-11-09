package io.languify.infra.realtime;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class Realtime {
  private final String REALTIME_SECRET;
  private final String REALTIME_URI;

  private WebSocket socket;

  public Realtime(String secret, String uri) {
    this.REALTIME_SECRET = secret;
    this.REALTIME_URI = uri;
  }

  public void connect() {
    HttpClient client = HttpClient.newHttpClient();

    WebSocket.Builder builder =
        client
            .newWebSocketBuilder()
            .header("Authorization", "Bearer " + this.REALTIME_SECRET)
            .header("OpenAI-Beta", "realtime=v1");

    this.socket =
        builder
            .buildAsync(
                URI.create(this.REALTIME_SECRET),
                new WebSocket.Listener() {
                  @Override
                  public void onOpen(WebSocket socket) {
                    System.out.println("Connected to OpenAI Realtime API");
                    WebSocket.Listener.super.onOpen(socket);
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

  public void disconnect() {
    this.socket.sendClose(WebSocket.NORMAL_CLOSURE, "Closing");
  }
}
