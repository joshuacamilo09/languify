package io.languify.identity.user.repository;

import io.languify.identity.user.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findUserById(UUID id);

  Optional<User> findByEmail(String email);
}
