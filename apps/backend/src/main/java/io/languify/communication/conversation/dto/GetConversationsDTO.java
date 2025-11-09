package io.languify.communication.conversation.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class GetConversationsDTO {
  private String id;
  private String userId;
  private String title;
  private String fromLanguage;
  private String toLanguage;
  private LocalDate createdAt;
}
