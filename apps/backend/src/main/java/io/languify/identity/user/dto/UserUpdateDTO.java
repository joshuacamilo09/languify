package io.languify.identity.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// Vai servir para atualizar apenas os campos editáveis do utilizador (nao id, email, etc...)
public class UserUpdateDTO {
  private String nome;
  private String native_idiom;
}
