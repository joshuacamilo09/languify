package io.languify.infra.security.filter;

import io.languify.identity.auth.model.Session;
import io.languify.identity.user.model.User;
import io.languify.identity.user.repository.UserRepository;
import io.languify.infra.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwt;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain chain)
      throws ServletException, IOException {

    String bearer = req.getHeader("Authorization");

    if (bearer == null || !bearer.startsWith("Bearer ")) {
      chain.doFilter(req, res);
      return;
    }

    String token = bearer.substring(7);

    try {
      if (jwt.isInvalid(token)) {
        chain.doFilter(req, res);
        return;
      }

      String userId = jwt.getSubject(token);

        if (userId == null) {
            chain.doFilter(req, res);
            return;
        }


        Optional<User> optionalUser = userRepository.findUserById(UUID.fromString(userId));

      if (optionalUser.isEmpty()) {
        chain.doFilter(req, res);
        return;
      }

      Session session = new Session(optionalUser.get());

      UsernamePasswordAuthenticationToken authorized =
          new UsernamePasswordAuthenticationToken(session, null, null);

      authorized.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
      SecurityContextHolder.getContext().setAuthentication(authorized);

      chain.doFilter(req, res);
    } catch (Exception e) {
      chain.doFilter(req, res);
    }
  }
}
