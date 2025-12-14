package com.languify.communication.conversation.model;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "conversation_messages")
@Data
public class ConversationMessage {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String transcription;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String translation;

  @Column(name = "source_language", nullable = false, length = 10)
  private String sourceLanguage;

  @Column(name = "target_language", nullable = false, length = 10)
  private String targetLanguage;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MessageSpeaker speaker;

  @Column(name = "openai_response_id")
  private String openaiResponseId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @PrePersist
  private void onCreate() {
    this.createdAt = Instant.now();
  }
}
