package org.rest.languifybackend.User.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.rest.languifybackend.Settings.Model.Settings;
import org.rest.languifybackend.Settings.Model.VoiceType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table (name = "users")
public class User implements UserDetails
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    private String nome;

    @NotNull
    @Column(unique = true)
    private String  email;

    @Column
    @NotNull
    private String password;

    @Column
    private String googleId;

    @Column
    private String native_idiom;

    @Column
    private LocalDate registerDate;
    //PARA DEFINIR AUTOMATICAMENTE A DATA REGISTRO
    @PrePersist
    public void prePersist() {
        this.registerDate = LocalDate.now();
    }

    @Enumerated(EnumType.STRING)
    private Role role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        if (role == Role.ADMIN)
        {
            return List.of(
                    new SimpleGrantedAuthority("ROLE_"+Role.ADMIN.getRole()),
                    new SimpleGrantedAuthority("ROLE_"+Role.USER.getRole())
            );
        }
        else
        {
            return List.of(new SimpleGrantedAuthority("ROLE_"+Role.USER.getRole()));
        }
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
