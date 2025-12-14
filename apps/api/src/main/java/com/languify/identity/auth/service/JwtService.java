package com.languify.identity.auth.service;

import java.sql.Date;
import java.util.Optional;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Service;
import com.languify.identity.auth.config.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  private final SecretKey secretKey;
  private final long expiration;

  public JwtService(JwtProperties jwtProperties) {
    this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    this.expiration = jwtProperties.getExpiration();
  }

  public String generateToken(UUID userId) {
    return Jwts.builder().subject(userId.toString())
        .issuedAt(new Date(System.currentTimeMillis() + expiration)).signWith(secretKey).compact();
  }

  public Optional<UUID> validateAndGetUserId(String token) {
    try {
      String userId = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
          .getPayload().getSubject();

      return Optional.of(UUID.fromString(userId));
    } catch (Exception ex) {
      return Optional.empty();
    }
  }
}
