package io.languify.communication.conversation.dto;

import lombok.Data;

@Data
public class StartConversationDTO {
  private String sourceLanguage;
  private String targetLanguage;
}
