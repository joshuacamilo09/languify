package org.rest.languifybackend.User.Model;

import lombok.Getter;

@Getter
public enum Role
{
    USER("USER"),
    ADMIN("ADMIN");
    private final String role;
    Role( String role )
    {
        this.role = role;
    }
}
