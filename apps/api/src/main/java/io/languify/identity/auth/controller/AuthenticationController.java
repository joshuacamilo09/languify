package io.languify.identity.auth.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.languify.identity.auth.dto.SignDTO;
import io.languify.identity.auth.dto.SignWithGoogleDTO;
import io.languify.identity.auth.dto.SignResponseDTO;
import io.languify.identity.auth.model.Session;
import io.languify.identity.user.model.User;
import io.languify.identity.user.repository.UserRepository;
import io.languify.identity.user.service.UserService;
import io.languify.infra.logging.Logger;
import io.languify.infra.security.service.JwtService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
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

  @PostMapping("/sign")
  public ResponseEntity<SignResponseDTO> sign(@RequestBody SignDTO req) {
      User user = this.userRepository.findByEmail(req.getEmail()).orElse(null);

      if (user == null) {
          user = this.userService.createUser(req.getEmail(), req.getPassword(), null, null, null);
      }

      String token = this.jwt.createToken(user.getId());
      return ResponseEntity.ok(new SignResponseDTO(token));
  }

  @PostMapping("/sign/google")
  public ResponseEntity<SignResponseDTO> signWithGoogle(
      @RequestBody SignWithGoogleDTO req) {
      GoogleIdTokenVerifier verifier =
              new GoogleIdTokenVerifier.Builder(
                      new NetHttpTransport(), GsonFactory.getDefaultInstance())
                      .setAudience(Collections.singletonList(clientId))
                      .build();

      GoogleIdToken token = null;

      try {
           token = verifier.verify(req.getIdToken());
      } catch (Exception e) {
        Logger.error(log, "Failed to verify Google ID token", e);
      }

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
                      .orElseGet(() -> this.userService.createUser(email, null, givenName, familyName, picture));

      String signed = this.jwt.createToken(user.getId());
      return ResponseEntity.ok(new SignResponseDTO(signed));
  }
}
