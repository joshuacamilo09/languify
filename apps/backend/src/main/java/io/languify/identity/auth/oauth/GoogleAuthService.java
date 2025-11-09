package io.languify.identity.auth.oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.languify.identity.auth.config.JwtService;
import io.languify.identity.auth.model.AuthResponse;
import io.languify.identity.user.model.User;
import io.languify.identity.user.repository.UserRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {
  private final UserRepository userRepository;
  private final JwtService jwtService;

  @Value("${google.client.id}")
  private String GOOGLE_CLIENT_ID;

  public AuthResponse authenticationWithGoogle(String token) {
    try {
      GoogleIdTokenVerifier verifier =
          new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
              .setAudience(Collections.singletonList(GOOGLE_CLIENT_ID))
              .build();

      GoogleIdToken idToken = verifier.verify(token);

      if (idToken != null) {
        GoogleIdToken.Payload payload = idToken.getPayload();

        String email = payload.getEmail();
        String name =
            payload.get("name") != null ? (String) payload.get("name") : payload.getEmail();
        String googleId = payload.getSubject();

        var user =
            userRepository
                .findUserByEmail(email)
                .orElseGet(
                    () -> {
                      var newUser =
                          User.builder()
                              .nome(name)
                              .email(email)
                              .googleId(googleId)
                              .registerDate(java.time.LocalDate.now())
                              .role(Role.USER)
                              .native_idiom("English (Default)")
                              .build();

                      return userRepository.save(newUser);
                    });

        String jwtToken = jwtService.generateToken(user);

        return AuthResponse.builder().token(jwtToken).build();
      } else {
        throw new RuntimeException("Token invalido ou expirado");
      }
    } catch (Exception e) {
      throw new RuntimeException("Erro na autentticação com Google: " + e.getMessage(), e);
    }
  }
}
