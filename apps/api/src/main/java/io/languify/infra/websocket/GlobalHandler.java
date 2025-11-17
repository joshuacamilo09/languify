package io.languify.infra.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.languify.communication.conversation.controller.websocket.ConversationHandler;
import io.languify.identity.auth.model.Session;
import io.languify.identity.user.model.User;
import io.languify.infra.logging.Logger;
import io.languify.infra.websocket.dto.WebSocketMessageDTO;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalHandler extends TextWebSocketHandler {
  private final ConversationHandler conversationHandler;
  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    Logger.info(
        log,
        "WebSocket connection established",
        "sessionId",
        session.getId(),
        "user",
        describeSessionOwner(session));

    super.afterConnectionEstablished(session);
  }

  @Override
  public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status)
      throws Exception {
    Logger.info(
        log,
        "WebSocket connection closed",
        "sessionId",
        session.getId(),
        "statusCode",
        status.getCode(),
        "reason",
        status.getReason(),
        "user",
        describeSessionOwner(session));

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
      Logger.error(
          log,
          "WebSocket message error",
          ex,
          "sessionId",
          session.getId(),
          "payload",
          payload,
          "user",
          describeSessionOwner(session));

      throw ex;
    }
  }

  @Override
  public void handleTransportError(
      @NonNull WebSocketSession session, @NonNull Throwable exception) throws Exception {
    Logger.error(
        log,
        "WebSocket transport error",
        exception,
        "sessionId",
        session.getId(),
        "user",
        describeSessionOwner(session));
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
