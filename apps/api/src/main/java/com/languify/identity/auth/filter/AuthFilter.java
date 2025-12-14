package com.languify.identity.auth.filter;

import java.io.IOException;
import java.util.Optional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.languify.identity.auth.model.Session;
import com.languify.identity.auth.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {
  private final AuthService authService;

  @Override
  public void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
      FilterChain filterChain) throws ServletException, IOException {
    String header = req.getHeader("Authorization");

    if (header == null) {
      filterChain.doFilter(req, res);
      return;
    }

    Optional<String> parsed = authService.parseToken(header);

    if (parsed.isEmpty()) {
      filterChain.doFilter(req, res);
      return;
    }

    try {
      Optional<Session> optionalSession = authService.getSessionFromToken(parsed.get());

      optionalSession.ifPresent(session -> {
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(session, null, session.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
      });
    } catch (Exception _) {
    }

    filterChain.doFilter(req, res);
  }
}
