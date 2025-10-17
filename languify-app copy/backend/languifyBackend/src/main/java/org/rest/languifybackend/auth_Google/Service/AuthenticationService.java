package org.rest.languifybackend.auth_Google.Service;

import lombok.RequiredArgsConstructor;
import org.rest.languifybackend.User.Model.Role;
import org.rest.languifybackend.User.Model.User;
import org.rest.languifybackend.User.UserRepo.UserRepository;
import org.rest.languifybackend.auth_Google.Model.AuthRequest;
import org.rest.languifybackend.auth_Google.Model.AuthResponse;
import org.rest.languifybackend.auth_Google.Model.RegisterRequest;
import org.rest.languifybackend.auth_Google.config.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthenticationService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;

    public AuthResponse register (RegisterRequest Request)
    {
        var user = User.builder()
                .nome(Request.getName())
                .email(Request.getEmail())
                .password(passwordEncoder.encode(Request.getPassword()))
                .native_idiom(Request.getNative_idiom())
                .RegisterDate(LocalDate.now())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        var token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse authenticate(AuthRequest Request)
    {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        Request.getEmail(),
                        Request.getPassword()
                )
        );

        var user = userRepository.findUserByEmail(Request.getEmail())
                .orElseThrow();

        var token = jwtService.generateToken(user);

        return AuthResponse.builder().token(token).build();
    }
}