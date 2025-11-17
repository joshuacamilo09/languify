package io.languify.identity.user.service;

import io.languify.identity.user.model.User;
import io.languify.identity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;

  public User createUser(String email, String password, String firstName, String lastName, String image) {
    User user = new User();

    user.setEmail(email);
    user.setPassword(password != null ? this.passwordEncoder.encode(password) : null);
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setImage(image);

    return this.repository.save(user);
  }
}
