package io.languify.identity.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignDTO {
    private String firstName;
    private String lastName;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O formato do email é inválido")
    private String email;

    @NotBlank(message = "A password é obrigatória")
    private String password;
}
