package io.languify.identity.auth.model;

import io.languify.identity.user.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Session {
  private final User user;
}
