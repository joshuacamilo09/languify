package com.languify.communication.conversation.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class GetConversationsDTO {
  private UUID id;
  private UUID userId;
  private String title;
  private String summary;
  private String fromLanguage;
  private String toLanguage;
  private Instant createdAt;

  public GetConversationsDTO(
      UUID id,
      UUID userId,
      String title,
      String summary,
      String fromLanguage,
      String toLanguage,
      Instant createdAt) {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.summary = summary;
    this.fromLanguage = fromLanguage;
    this.toLanguage = toLanguage;
    this.createdAt = createdAt;
  }
}
