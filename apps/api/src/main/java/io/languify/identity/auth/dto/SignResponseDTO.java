package io.languify.identity.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignResponseDTO {
  private String token;
  private String firstName;
  private String lastName;
  private String email;

  public SignResponseDTO(String token) {
    this.token = token;
  }
}
