package org.rest.languifybackend.auth_Google.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rest.languifybackend.Settings.Model.Settings;
import org.rest.languifybackend.User.Model.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest
{
    private String name;
    private String email;
    private String password;
    private String native_idiom;
}
