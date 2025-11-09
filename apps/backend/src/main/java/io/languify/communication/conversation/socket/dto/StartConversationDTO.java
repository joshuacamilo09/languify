package io.languify.communication.conversation.socket.dto;

import lombok.Data;

@Data
public class StartConversationDTO {
  private String fromLanguage;
  private String toLanguage;
}
