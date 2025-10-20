package org.rest.languifybackend.User.UserRepo;

import org.rest.languifybackend.User.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findUserByEmail(String email);
    Optional<User> findByEmail(String email);
}
