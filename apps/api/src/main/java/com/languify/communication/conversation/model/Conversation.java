package com.languify.communication.conversation.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.languify.identity.user.model.User;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "conversations")
@Data
public class Conversation {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String title;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ConversationState state;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id", nullable = false)
  private User author;

  @Column(name = "source_language", length = 10)
  private String sourceLanguage;

  @Column(name = "target_language", length = 10)
  private String targetLanguage;

  @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ConversationMessage> messages = new ArrayList<>();

  @PrePersist
  private void onCreate() {
    this.createdAt = Instant.now();
  }
}
