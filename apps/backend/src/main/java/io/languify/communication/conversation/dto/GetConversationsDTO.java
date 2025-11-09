package io.languify.communication.conversation.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class GetConversationsDTO {
  private String id;
  private String userId;
  private String title;
  private String summary;
  private String fromLanguage;
  private String toLanguage;
  private LocalDate createdAt;

  public GetConversationsDTO(
      String id,
      String userId,
      String title,
      String summary,
      String fromLanguage,
      String toLanguage,
      LocalDate createdAt) {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.summary = summary;
    this.fromLanguage = fromLanguage;
    this.toLanguage = toLanguage;
    this.createdAt = createdAt;
  }
}
