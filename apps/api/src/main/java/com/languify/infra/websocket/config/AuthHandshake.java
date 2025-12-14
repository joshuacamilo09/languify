package com.languify.infra.websocket.config;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import com.languify.identity.auth.model.Session;
import com.languify.identity.auth.service.AuthService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthHandshake implements HandshakeInterceptor {
  private final AuthService authService;

  @Override
  public boolean beforeHandshake(ServerHttpRequest req, ServerHttpResponse res,
      WebSocketHandler handler, Map<String, Object> attrs) throws Exception {
    List<String> headers = req.getHeaders().get("Authorization");
    if (headers == null || headers.isEmpty())
      return false;

    Optional<String> parsed = authService.parseToken(headers.get(0));

    if (parsed.isEmpty())
      return false;

    Optional<Session> session = authService.getSessionFromToken(parsed.get());
    if (session.isEmpty())
      return false;

    attrs.put("session", session.get());
    return true;
  }

  @Override
  public void afterHandshake(ServerHttpRequest req, ServerHttpResponse res,
      WebSocketHandler handler, Exception ex) {}
}
