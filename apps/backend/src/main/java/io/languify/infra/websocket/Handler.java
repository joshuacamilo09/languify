package io.languify.infra.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.languify.identity.auth.model.Session;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

public abstract class Handler {
  private final ObjectMapper mapper = new ObjectMapper();

  public abstract void handleSegment(String segment, JsonNode data, WebSocketSession session);

  public Session extractSessionFromWebSocketSession(WebSocketSession session) {
    return (Session) session.getAttributes().get("session");
  }

  public void emit(
      String event, Map<String, Object> data, String userId, WebSocketSession session) {
    try {
      HashMap<String, Object> message = new HashMap<>();
      message.put("event", event);

      HashMap<String, Object> d = new HashMap<>();
      d.put("userId", userId);

      if (data != null) {
        d.putAll(data);
      }

      message.put("data", d);
      session.sendMessage(new TextMessage(this.mapper.writeValueAsString(message)));
    } catch (Exception e) {
      // Empty
    }
  }
}
