package io.languify.communication.chat.model;

import lombok.Getter;

@Getter
public enum Direction {
  USER_TO_AI,
  AI_TO_USER,
  USER_TO_USER;
  private final String directiomname;

  Direction() {
    this.directiomname = this.name();
  }
}
