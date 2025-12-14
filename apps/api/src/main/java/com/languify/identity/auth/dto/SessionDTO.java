package com.languify.identity.auth.dto;

import com.languify.identity.auth.model.Session;
import com.languify.identity.user.dto.UserDTO;

public record SessionDTO(UserDTO user) {
  public static SessionDTO from(Session session) {
    return new SessionDTO(UserDTO.from(session.getUser()));
  }
}
