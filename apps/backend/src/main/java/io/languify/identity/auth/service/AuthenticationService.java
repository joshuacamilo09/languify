package io.languify.identity.auth.service;

import io.languify.identity.auth.config.JwtService;
import io.languify.identity.auth.model.AuthRequest;
import io.languify.identity.auth.model.AuthResponse;
import io.languify.identity.auth.model.RegisterRequest;
import io.languify.identity.user.model.User;
import io.languify.identity.user.repository.UserRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authManager;

  public AuthResponse register(RegisterRequest Request) {
    var user =
        User.builder()
            .nome(Request.getName())
            .email(Request.getEmail())
            .password(passwordEncoder.encode(Request.getPassword()))
            .native_idiom(Request.getNative_idiom())
            .registerDate(LocalDate.now())
            .role(Role.USER)
            .build();

    userRepository.save(user);

    var token = jwtService.generateToken(user);
    return AuthResponse.builder().token(token).build();
  }

  public AuthResponse authenticate(AuthRequest Request) {
    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(Request.getEmail(), Request.getPassword()));

    var user = userRepository.findUserByEmail(Request.getEmail()).orElseThrow();

    var token = jwtService.generateToken(user);

    return AuthResponse.builder().token(token).build();
  }
}
