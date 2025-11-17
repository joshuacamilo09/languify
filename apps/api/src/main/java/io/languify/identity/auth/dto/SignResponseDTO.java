package io.languify.identity.auth.dto;

import lombok.Data;

@Data
public class SignResponseDTO {
  private String token;

  public SignResponseDTO(String token) {
    this.token = token;
  }
}
