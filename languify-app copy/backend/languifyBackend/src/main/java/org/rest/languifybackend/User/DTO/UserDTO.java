package org.rest.languifybackend.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

//Este UserDTO vai servir para retornar dados pubilcos e seguros do utilizador sem expor senha ou tokens
public class UserDTO
{
    private Long id;
    private String nome;
    private String email;
    private String native_Idiom;
    private LocalDate RegisterDate;
}
