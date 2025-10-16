package org.rest.languifybackend.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rest.languifybackend.Settings.Model.Settings;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

//Vai servir para atualizar apenas os campos editáveis do utilizador (nao id, email, etc...)
public class UserUpdateDTO
{
    private String nome;
    private String native_Idiom;
    private Settings settings;
}
