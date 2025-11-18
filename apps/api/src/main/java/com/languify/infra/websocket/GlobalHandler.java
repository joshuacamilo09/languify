package com.languify.infra.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.languify.communication.conversation.controller.websocket.ConversationHandler;
import com.languify.identity.auth.model.Session;
import com.languify.identity.user.model.User;
import com.languify.infra.websocket.dto.WebSocketMessageDTO;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.Base64;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalHandler extends AbstractWebSocketHandler {
  private final ConversationHandler conversationHandler;
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {

    super.afterConnectionEstablished(session);
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status)
      throws Exception {
    log.info("WebSocket connection closed | sessionId={} user={} status={}",
        session.getId(),
        describeSessionOwner(session),
        status);

    conversationHandler.cleanupConnectionState(session);

    super.afterConnectionClosed(session, status);
  }

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message)
      throws Exception {
    String payload = message.getPayload();

    try {
      WebSocketMessageDTO dto = this.mapper.readValue(payload, WebSocketMessageDTO.class);


      String[] segments = dto.getEvent().split(":", 2);
      String resource = segments[0];

      if (!Objects.equals(resource, "conversation")) {
        return;
      }

      String segment = segments[1];
      conversationHandler.handleSegment(segment, dto.getData(), session);
    } catch (Exception ex) {
      log.error(
          "WebSocket message error | sessionId={} user={} payload={}",
          session.getId(),
          describeSessionOwner(session),
          payload,
          ex);

      throw ex;
    }
  }

  @Override
  protected void handleBinaryMessage(@NonNull WebSocketSession session, @NonNull BinaryMessage message)
      throws Exception {
    try {
      byte[] audioData = message.getPayload().array();
      String base64Audio = Base64.getEncoder().encodeToString(audioData);

      conversationHandler.handleBinaryAudio(base64Audio, session);
    } catch (Exception ex) {
      log.error(
          "WebSocket binary message error | sessionId={} user={}",
          session.getId(),
          describeSessionOwner(session),
          ex);

      throw ex;
    }
  }

  @Override
  public void handleTransportError(
      @NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
    log.error(
        "WebSocket transport error | sessionId={} user={}",
        session.getId(),
        describeSessionOwner(session),
        exception);

    super.handleTransportError(session, exception);
  }

  private String describeSessionOwner(WebSocketSession session) {
    Object attribute = session.getAttributes().get("session");
    if (!(attribute instanceof Session s) || s.getUser() == null) {
      return "user=unknown";
    }

    User user = s.getUser();
    return "userId=" + user.getId() + ", email=" + user.getEmail();
  }
}
