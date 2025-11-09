package io.languify.infra.socket;

import com.fasterxml.jackson.databind.JsonNode;
import io.languify.identity.auth.model.Session;
import org.springframework.web.socket.WebSocketSession;

public abstract class Handler {
  public abstract void handleSegment(String segment, JsonNode data, WebSocketSession session);

  public Session extractSessionFromWebSocketSession(WebSocketSession session) {
    return (Session) session.getAttributes().get("session");
  }
}
