package io.languify.identity.user.service;

import io.languify.identity.user.dto.UserDTO;
import io.languify.identity.user.dto.UserUpdateDTO;
import io.languify.identity.user.model.User;
import io.languify.identity.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));
  }

  public UserDTO updateUser(UserUpdateDTO dto) {
    User CurrentUser = getCurrentUser();

    if (dto.getNome() != null) CurrentUser.setNome(dto.getNome());
    if (dto.getNative_idiom() != null) CurrentUser.setNative_idiom(dto.getNative_idiom());

    userRepository.save(CurrentUser);

    return UserDTO.builder()
        .id(CurrentUser.getUserId())
        .nome(CurrentUser.getNome())
        .email(CurrentUser.getEmail())
        .native_idiom(CurrentUser.getNative_idiom())
        .RegisterDate(CurrentUser.getRegisterDate())
        .build();
  }

  public List<User> getAllusers() {
    return userRepository.findAll();
  }

  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }

  public void deleteUserById(Long id) {
    userRepository.deleteById(id);
  }
}
