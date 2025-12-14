package com.languify.identity.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.languify.identity.user.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {
  public Optional<User> findByEmail(String email);

  public boolean existsByEmail(String email);

  public boolean existsByUsername(String username);
}
