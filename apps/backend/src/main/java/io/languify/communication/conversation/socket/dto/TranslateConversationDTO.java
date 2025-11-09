package io.languify.communication.conversation.socket.dto;

import lombok.Data;

@Data
public class TranslateConversationDTO {
  private String fromLanguage;
  private String toLanguage;
}
