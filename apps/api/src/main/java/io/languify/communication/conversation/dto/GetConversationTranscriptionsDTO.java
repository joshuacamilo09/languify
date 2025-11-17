package io.languify.communication.conversation.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class GetConversationTranscriptionsDTO {
  private UUID id;
  private UUID conversationId;
  private String originalTranscription;
  private String translatedTranscription;
  private Instant createdAt;

  public GetConversationTranscriptionsDTO(
      UUID id,
      UUID conversationId,
      String originalTranscription,
      String translatedTranscription,
      Instant createdAt) {
    this.id = id;
    this.conversationId = conversationId;
    this.originalTranscription = originalTranscription;
    this.translatedTranscription = translatedTranscription;
    this.createdAt = createdAt;
  }
}
