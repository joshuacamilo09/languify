package com.languify.identity.user.service;

import java.util.Optional;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.languify.identity.user.model.User;
import com.languify.identity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  public Optional<User> get(UUID id) {
    return userRepository.findById(id);
  }

  public Optional<User> getByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  public User create(String email, String username, String password) {
    User user = new User();

    user.setEmail(email);
    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));

    return userRepository.save(user);
  }
}
