package org.rest.languifybackend.auth_Google.Controller;

import lombok.RequiredArgsConstructor;
import org.rest.languifybackend.auth_Google.Model.*;
import org.rest.languifybackend.auth_Google.Service.AuthenticationService;
import org.rest.languifybackend.auth_Google.oauth.GoogleAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("languify/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController
{
    private final AuthenticationService authService;
    private final GoogleAuthService googleAuthService;

    @PostMapping("/login/{id}")
    public ResponseEntity<AuthResponse> authenticate (@PathVariable Long id, @RequestBody AuthRequest request)
    {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register (@RequestBody RegisterRequest request)
    {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleLogin (@RequestBody GoogleAuthRequest request)
    {
        return ResponseEntity.ok(googleAuthService.authenticationWithGoogle(request.getIdToken()));
    }
}