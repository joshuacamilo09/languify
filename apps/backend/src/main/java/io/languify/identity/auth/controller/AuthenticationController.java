package io.languify.identity.auth.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.languify.identity.auth.dto.SignWithGoogleDTO;
import io.languify.identity.auth.model.Session;
import io.languify.identity.user.model.User;
import io.languify.identity.user.repository.UserRepository;
import io.languify.identity.user.service.UserService;
import io.languify.infra.security.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class AuthenticationController {
  private final JwtService jwt;

  private final UserService userService;
  private final UserRepository userRepository;

  @Value("${google.client.id}")
  private String clientId;

  @GetMapping("/session")
  public ResponseEntity<Session> getSession(@AuthenticationPrincipal Session session) {
    return ResponseEntity.ok(session);
  }

  @PostMapping("/sign/google")
  public ResponseEntity<?> signWithGoogle(
      @RequestBody SignWithGoogleDTO request, HttpServletResponse response) {
    try {
      GoogleIdTokenVerifier verifier =
          new GoogleIdTokenVerifier.Builder(
                  new NetHttpTransport(), GsonFactory.getDefaultInstance())
              .setAudience(Collections.singletonList(clientId))
              .build();

      GoogleIdToken token = verifier.verify(request.getIdToken());

      if (token == null) {
        return ResponseEntity.badRequest().build();
      }

      GoogleIdToken.Payload payload = token.getPayload();

      String email = payload.getEmail();
      String givenName = (String) payload.get("given_name");
      String familyName = (String) payload.get("family_name");
      String picture = (String) payload.get("picture");

      User user =
          userRepository
              .findByEmail(email)
              .orElseGet(() -> this.userService.createUser(email, givenName, familyName, picture));

      String signed = jwt.createToken(user.getId().toString());

      Cookie cookie = new Cookie("token", signed);
      cookie.setHttpOnly(true);
      cookie.setSecure(true);
      cookie.setPath("/");
      cookie.setMaxAge(60 * 60 * 24 * 7);

      response.addCookie(cookie);

      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.internalServerError().build();
    }
  }
}
