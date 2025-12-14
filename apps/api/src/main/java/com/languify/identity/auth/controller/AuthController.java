package com.languify.identity.auth.controller;

import java.util.Optional;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.languify.identity.auth.dto.GetSessionResponse;
import com.languify.identity.auth.dto.SessionDTO;
import com.languify.identity.auth.dto.SignInRequest;
import com.languify.identity.auth.dto.SignResponse;
import com.languify.identity.auth.dto.SignUpRequest;
import com.languify.identity.auth.model.Session;
import com.languify.identity.auth.service.JwtService;
import com.languify.identity.user.model.User;
import com.languify.identity.user.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final UserService userService;

  @GetMapping("/session")
  public ResponseEntity<GetSessionResponse> getSession(@AuthenticationPrincipal Session session) {
    return ResponseEntity.ok(new GetSessionResponse(SessionDTO.from(session)));
  }

  @PostMapping("/sign-in")
  public ResponseEntity<SignResponse> signIn(@RequestBody SignInRequest req) {
    Optional<User> optionalUser = userService.getByEmail(req.email());
    if (optionalUser.isEmpty())
      return ResponseEntity.ok().build();

    User user = optionalUser.get();
    if (!passwordEncoder.matches(req.password(), user.getPassword()))
      return ResponseEntity.ok().build();

    SignResponse res = generateSignResponse(user.getId());
    return ResponseEntity.ok(res);
  }

  @PostMapping("/sign-up")
  public ResponseEntity<SignResponse> signUp(@RequestBody SignUpRequest req) {
    boolean byEmail = userService.existsByEmail(req.email());
    boolean byUsername = userService.existsByUsername(req.username());

    if (byEmail || byUsername)
      return ResponseEntity.ok().build();

    User user = userService.create(req.email(), req.username(), req.password());
    SignResponse res = generateSignResponse(user.getId());
    return ResponseEntity.ok(res);
  }

  private SignResponse generateSignResponse(UUID userId) {
    String token = jwtService.generateToken(userId);
    return new SignResponse(token);
  }
}
