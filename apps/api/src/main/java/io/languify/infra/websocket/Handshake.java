package io.languify.infra.websocket;

import io.languify.identity.auth.model.Session;
import io.languify.identity.user.model.User;
import io.languify.identity.user.repository.UserRepository;
import io.languify.infra.security.service.JwtService;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class Handshake implements HandshakeInterceptor {
  private final JwtService jwt;
  private final UserRepository userRepository;

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest req,
      ServerHttpResponse _res,
      WebSocketHandler _handler,
      Map<String, Object> attrs)
      throws Exception {
    String token = req.getHeaders().getFirst("Authorization");
    Optional<String> normalized = normalizeToken(token);
    if (normalized.isEmpty()) return false;

    String userId = jwt.getSubject(normalized.get());
    Optional<User> user = this.userRepository.findUserById(UUID.fromString(userId));

    if (user.isPresent()) {
      attrs.put("session", new Session(user.get()));
      return true;
    }

    return false;
  }

  private Optional<String> normalizeToken(String token) {
    if (token == null || !token.startsWith("Bearer ")) {
      return Optional.empty();
    }

    String cleaned = token.replace("Bearer ", "");

    if (this.jwt.isInvalid(cleaned)) {
      return Optional.empty();
    }

    return Optional.of(cleaned);
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest req,
      ServerHttpResponse res,
      WebSocketHandler handler,
      Exception exception) {}
}
