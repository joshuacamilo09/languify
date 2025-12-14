package com.languify.infra.websocket.handler;

import java.util.UUID;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.languify.identity.auth.model.Session;
import com.languify.infra.websocket.dto.WebSocketMessage;
import com.languify.infra.websocket.service.WebSocketManager;

@Component
public abstract class WebSocketHandler extends TextWebSocketHandler {
  private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
  private final ObjectMapper objectMapper = new ObjectMapper();

  protected final WebSocketManager manager;

  public WebSocketHandler(WebSocketManager manager) {
    this.manager = manager;
  }

  @Override
  public final void afterConnectionEstablished(WebSocketSession ws) {
    Session session = (Session) ws.getAttributes().get("session");
    UUID userId = session.getUser().getId();

    manager.addSession(ws, userId);

    try {
      onConnectionEstablished(ws, session);
    } catch (Exception e) {
      manager.removeSession(userId);
      logger.error("Something went wrong while establishing WebSocket connection.", e);
    }
  }

  @Override
  public final void handleTextMessage(WebSocketSession ws, TextMessage message) {
    Session session = (Session) ws.getAttributes().get("session");

    try {
      WebSocketMessage wsMessage =
          objectMapper.readValue(message.getPayload(), WebSocketMessage.class);

      handleMessage(wsMessage, ws, session);
    } catch (Exception ex) {
      logger.error("Something went wrong while handling WebSocket Message.", ex);
    }
  }

  @Override
  public final void afterConnectionClosed(WebSocketSession ws, CloseStatus status) {
    Session session = (Session) ws.getAttributes().get("session");

    try {
      onConnectionClosed(ws, session, status);
    } catch (Exception ex) {
      logger.error("Something went wrong while closing WebSocket connection.", ex);
    }

    manager.removeSession(session.getUser().getId());
  }

  protected void onConnectionEstablished(WebSocketSession ws, Session session) throws Exception {}

  protected abstract void handleMessage(WebSocketMessage message, WebSocketSession ws,
      Session session) throws Exception;

  protected void onConnectionClosed(WebSocketSession ws, Session session, CloseStatus status)
      throws Exception {}
}
