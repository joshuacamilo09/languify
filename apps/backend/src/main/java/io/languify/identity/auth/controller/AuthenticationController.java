package io.languify.identity.auth.controller;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
class AuthenticationController {

  @Value("${google.client.id}")
  private String clientId;

  @Value("${google.redirect.uri}")
  private String redirectUri;

  @PostMapping("/sign/google")
  public ResponseEntity<Void> getGoogleAuthUrl() {
    String url =
        UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("response_type", "code")
            .queryParam("scope", "openid email profile")
            .queryParam("access_type", "offline")
            .toUriString();

    return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(authUrl)).build();
  }
}
