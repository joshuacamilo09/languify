package com.languify.infra.realtime.client;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.languify.infra.realtime.config.RealtimeConfig;
import com.languify.infra.realtime.config.RealtimeProperties;
import com.languify.infra.realtime.dto.AudioBufferAppendEvent;
import com.languify.infra.realtime.dto.AudioBufferCommitEvent;
import com.languify.infra.realtime.dto.InputAudioBufferClearEvent;
import com.languify.infra.realtime.dto.RealtimeClientEvent;
import com.languify.infra.realtime.dto.RealtimeServerEvent;
import com.languify.infra.realtime.dto.ResponseCancelEvent;
import com.languify.infra.realtime.dto.ResponseCreateEvent;
import com.languify.infra.realtime.dto.SessionUpdateEvent;

public class RealtimeClient extends TextWebSocketHandler {
  private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
  private final ObjectMapper mapper = new ObjectMapper();

  private final RealtimeProperties properties;
  private final RealtimeConfig config;

  private final Consumer<RealtimeServerEvent> handler;
  private final RealtimeClientParams params;

  public RealtimeClient(RealtimeProperties properties, RealtimeConfig config,
      Consumer<RealtimeServerEvent> handler, RealtimeClientParams params) {
    super();
    this.properties = properties;
    this.config = config;
    this.handler = handler;
    this.params = params;
  }

  private WebSocketSession ws;
  private final CompletableFuture<Void> connection = new CompletableFuture<>();
  private volatile boolean ready = false;

  public CompletableFuture<Void> connect() {
    try {
      ready = false;
      String url = String.format("%s?model=%s", properties.getUrl(), properties.getModel());

      WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
      headers.add("Authorization", "Bearer " + properties.getSecret());
      headers.add("OpenAI-Beta", "realtime=v1");

      StandardWebSocketClient client = new StandardWebSocketClient();
      client.execute(this, headers, URI.create(url));

      return connection;
    } catch (Exception ex) {
      logger.error("Something went wrong while connecting to the WebSocket Server.", ex);
      connection.completeExceptionally(ex);

      return connection;
    }
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession ws) throws Exception {
    this.ws = ws;
    sendSessionUpdate();

    params.onConnect(this);
  }

  private void sendSessionUpdate() {
    send(new SessionUpdateEvent(config));
  }

  @Override
  public void handleTextMessage(WebSocketSession ws, TextMessage message) throws Exception {
    try {
      String payload = message.getPayload();
      RealtimeServerEvent event = mapper.readValue(payload, RealtimeServerEvent.class);
      logger.info("Realtime WS IN type={}", event.type());

      if ("session.created".equals(event.type())) {
        connection.complete(null);
        ready = true;
      } else if ("session.updated".equals(event.type())) {
        ready = true;
      }

      handler.accept(event);
    } catch (Exception ex) {
      logger.error("Something while wrong while handling WebSocket message.", ex);
    }
  }

  @Override
  public void handleTransportError(WebSocketSession ws, Throwable ex) throws Exception {
    logger.error("WebSocket Transport Error", ex);
    ready = false;
    connection.completeExceptionally(ex);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession ws, CloseStatus status) {
    this.ws = null;
    ready = false;
    params.onDisconnect(this);
  }

  public boolean sendAudioChunk(String audio) {
    return send(new AudioBufferAppendEvent(audio));
  }

  public boolean commitAudio() {
    return send(new AudioBufferCommitEvent());
  }

  public boolean createResponse() {
    return send(new ResponseCreateEvent());
  }

  public boolean updateSession(RealtimeConfig config) {
    return send(new SessionUpdateEvent(config));
  }

  public boolean cancelResponse(String responseId) {
    return send(new ResponseCancelEvent(responseId));
  }

  public boolean clearInputAudioBuffer() {
    return send(new InputAudioBufferClearEvent());
  }

  private boolean send(RealtimeClientEvent event) {
    if (!isConnected()) {
      logger.error("Trying to send event while Realtime client is not connected.");
      return false;
    }

    try {
      String data = mapper.writeValueAsString(event);
      logger.info("Realtime WS OUT type={}", event.getClass().getSimpleName());
      synchronized (this) {
        ws.sendMessage(new TextMessage(data));
      }
      return true;
    } catch (Exception ex) {
      logger.error("Something went wrong while sending message to WebSocket Server.", ex);
      return false;
    }
  }

  public void disconnect() {
    try {
      if (ws == null || !ws.isOpen()) {
        logger.error("Trying to disconnect without a WebSocket Session Session.");
        return;
      }

      ready = false;
      ws.close();
    } catch (Exception ex) {
      logger.error("Something went wrong while disconnecting WebSocket Session.", ex);
    }
  }

  public boolean isConnected() {
    return ws != null && ws.isOpen();
  }

  public boolean isReady() {
    return isConnected() && ready;
  }
}
