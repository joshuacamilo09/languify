package com.languify.identity.auth.model;

import com.languify.identity.user.model.User;
import lombok.Data;

@Data
public class Session {
    private User user;

    public Session(User user) {
        this.user = user;
    }
}
