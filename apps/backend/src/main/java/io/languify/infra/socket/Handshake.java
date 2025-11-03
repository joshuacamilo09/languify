package io.languify.infra.socket;

import io.languify.identity.auth.model.Session;
import io.languify.identity.user.model.User;
import io.languify.identity.user.repository.UserRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class Handshake implements HandshakeInterceptor {
  private final Jwt jwt;
  private final UserRepository userRepository;

  @Override
  public boolean beforeHandshake(
      ServerHttpRequest req,
      ServerHttpResponse res,
      WebSocketHandler handler,
      Map<String, Object> attrs)
      throws Exception {
    String token = req.getHeaders().getFirst("Authorization");
    Optional<String> normalized = normalizeToken(token);
    if (normalized.isEmpty()) return false;

    String userId = jwt.getSubject(normalized.get());
    Optional<User> user = this.userRepository.findUserById(userId);

    if (user.isPresent()) {
      attrs.put("session", new Session(user.get()));
      return true;
    }

    return false;
  }

  private Optional<String> normalizeToken(String token) {
    if (token == null || !token.startsWith("Bearer ") || !this.jwt.isValid(token)) {
      return Optional.empty();
    }

    return token.replace("Bearer ", "").describeConstable();
  }

  @Override
  public void afterHandshake(
      ServerHttpRequest req,
      ServerHttpResponse res,
      WebSocketHandler handler,
      Exception exception) {
    // Optional: called after handshake completes
  }
}
