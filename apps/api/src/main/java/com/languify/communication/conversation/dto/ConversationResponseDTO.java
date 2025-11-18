package com.languify.communication.conversation.dto;

public class ConversationResponseDTO {
  @lombok.Data
  @lombok.AllArgsConstructor
  public static class AudioDelta {
    private String audio;
  }

  @lombok.Data
  @lombok.AllArgsConstructor
  public static class TranslateState {
    private String state;
  }
}
