package io.languify.identity.auth.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.languify.identity.auth.dto.SignDTO;
import io.languify.identity.auth.dto.SignWithGoogleDTO;
import io.languify.identity.auth.dto.SignResponseDTO;
import io.languify.identity.auth.model.Session;
import io.languify.identity.user.model.User;
import io.languify.identity.user.repository.UserRepository;
import io.languify.identity.user.service.UserService;
import io.languify.infra.security.service.JwtService;
import java.util.Collections;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder; // Importante!
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class AuthenticationController {
    private final JwtService jwt;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Injetamos o codificador

    @Value("${google.client.id}")
    private String clientId;

    @GetMapping("/session")
    public ResponseEntity<Session> getSession(@AuthenticationPrincipal Session session) {
        return ResponseEntity.ok(session);
    }

    // REGISTO
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody SignDTO req) {
        try {
            if (this.userRepository.findByEmail(req.getEmail()).isPresent()) {
                return ResponseEntity.badRequest().body("Este email já está registado.");
            }

            // Nota: Assumo que o teu userService.createUser já encripta a password.
            // Se não encriptar, terias de mudar para: passwordEncoder.encode(req.getPassword())
            User user = this.userService.createUser(
                    req.getEmail(),
                    req.getPassword(),
                    req.getFirstName(),
                    req.getLastName(),
                    null
            );

            String token = this.jwt.createToken(user.getId());
            return ResponseEntity.ok(new SignResponseDTO(token, user.getFirstName(), user.getLastName(), user.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao registar.");
        }
    }

    // LOGIN CORRIGIDO
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody SignDTO req) {
        try {
            User user = this.userRepository.findByEmail(req.getEmail()).orElse(null);

            if (user == null) {
                return ResponseEntity.status(404).body("Email não encontrado.");
            }

            // AQUI ESTAVA O PROBLEMA
            // Em vez de user.getPassword().equals(...), usamos o matches:
            if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body("Password incorreta.");
            }

            String token = this.jwt.createToken(user.getId());
            return ResponseEntity.ok(new SignResponseDTO(token, user.getFirstName(), user.getLastName(), user.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao fazer login.");
        }
    }

    // GOOGLE (Mantém-se igual)
    @PostMapping("/sign/google")
    public ResponseEntity<SignResponseDTO> signWithGoogle(@RequestBody SignWithGoogleDTO req) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken token = verifier.verify(req.getIdToken());
            if (token == null) return ResponseEntity.badRequest().build();

            GoogleIdToken.Payload payload = token.getPayload();
            String email = payload.getEmail();
            String givenName = (String) payload.get("given_name");
            String familyName = (String) payload.get("family_name");
            String picture = (String) payload.get("picture");

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> this.userService.createUser(email, null, givenName, familyName, picture));

            String signed = this.jwt.createToken(user.getId());
            return ResponseEntity.ok(new SignResponseDTO(signed, user.getFirstName(), user.getLastName(), user.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}