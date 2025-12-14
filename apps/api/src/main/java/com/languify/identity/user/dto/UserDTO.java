package com.languify.identity.user.dto;

import java.util.UUID;
import com.languify.identity.user.model.User;

public record UserDTO(
    UUID id,
    String username,
    String email
) {
  public static UserDTO from(User user) {
    return new UserDTO(
        user.getId(),
        user.getUsername(),
        user.getEmail()
    );
  }
}
