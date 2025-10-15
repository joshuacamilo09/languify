package org.rest.languifybackend.auth_Google.Controller;

import lombok.RequiredArgsConstructor;
import org.rest.languifybackend.auth_Google.Model.AuthRequest;
import org.rest.languifybackend.auth_Google.Model.AuthResponse;
import org.rest.languifybackend.auth_Google.Model.GoogleAuthRequest;
import org.rest.languifybackend.auth_Google.Model.RegisterRequest;
import org.rest.languifybackend.auth_Google.Service.AuthenticationService;
import org.rest.languifybackend.auth_Google.oauth.GoogleAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/languify/v1/")
@RequiredArgsConstructor
public class AuthenticationController
{
    private final AuthenticationService authService;
    private final GoogleAuthService googleAuthService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate (@RequestBody AuthRequest request)
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