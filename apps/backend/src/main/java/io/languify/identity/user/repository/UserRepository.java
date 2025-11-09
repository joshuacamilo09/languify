package io.languify.identity.user.repository;

import io.languify.identity.user.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findUserById(String id);

  Optional<User> findByEmail(String email);
}
