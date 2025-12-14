package com.languify.identity.auth.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.languify.identity.auth.model.Session;
import com.languify.identity.user.model.User;
import com.languify.identity.user.service.UserService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final JwtService jwtService;
  private final UserService userService;

  public Optional<String> parseToken(String token) {
    if (!token.startsWith("Bearer"))
      return Optional.empty();

    String replaced = token.replace("Bearer ", "");
    if (replaced.length() == 0)
      return Optional.empty();

    return Optional.of(replaced);
  }

  public Optional<Session> getSessionFromToken(String token) {
    Optional<UUID> userId = jwtService.validateAndGetUserId(token);
    if (userId.isEmpty())
      return Optional.empty();

    Optional<User> optionalUser = userService.get(userId.get());

    if (optionalUser.isEmpty())
      return Optional.empty();

    Session session = new Session(optionalUser.get());
    return Optional.of(session);
  }
}
