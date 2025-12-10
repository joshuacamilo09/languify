package io.languify.infra.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  @Value("${jwt.secret}")
  private String JWT_SECRET;

  private SecretKey SECRET_KEY;

  public String createToken(UUID userId) {
    return Jwts.builder()
        .subject(userId.toString())
        .issuedAt(new Date())
        .expiration(Date.from(ZonedDateTime.now().plusMonths(1).toInstant()))
        .signWith(getSecretKey())
        .compact();
  }

  public String getSubject(String token) {
    return this.getClaims(token).getSubject();
  }

  public boolean isInvalid(String token) {
    try {
      return getClaims(token).getExpiration().before(new Date());
    } catch (Exception ex) {
      return true;
    }
  }

    private SecretKey getSecretKey() {
        if (this.SECRET_KEY == null) {
            byte[] bytes = Decoders.BASE64.decode(this.JWT_SECRET);
            this.SECRET_KEY = Keys.hmacShaKeyFor(bytes);
        }
        return this.SECRET_KEY;
    }


    private Claims getClaims(String token) {
    return Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).getPayload();
  }
}
