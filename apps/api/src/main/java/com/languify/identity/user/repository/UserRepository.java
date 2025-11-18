package com.languify.identity.user.repository;

import com.languify.identity.user.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findUserById(UUID id);

  Optional<User> findByEmail(String email);
}
