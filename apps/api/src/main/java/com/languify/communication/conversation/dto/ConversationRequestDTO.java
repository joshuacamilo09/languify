package com.languify.communication.conversation.dto;

import lombok.Data;

public class ConversationRequestDTO {
  @lombok.Data
  public static class Start {
    private String fromLanguage;
    private String toLanguage;
  }

  @lombok.Data
  public static class Data {
    private String audio;
  }
}
