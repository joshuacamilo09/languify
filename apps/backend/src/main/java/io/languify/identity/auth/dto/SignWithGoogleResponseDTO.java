package io.languify.identity.auth.dto;

import lombok.Data;

@Data
public class SignWithGoogleResponseDTO {
  private String token;

  public SignWithGoogleResponseDTO(String token) {
    this.token = token;
  }
}
