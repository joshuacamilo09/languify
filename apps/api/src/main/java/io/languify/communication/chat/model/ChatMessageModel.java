package io.languify.communication.chat.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "chat_messages")
@Data
public class ChatMessageModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "UUID")
  private UUID id;

  @Column private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChatMessageNature nature;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChatMessageStatus status;

  @Column(nullable = false)
  private Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "chat_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private ChatModel chat;

  @PrePersist
  private void prePersist() {
    this.createdAt = Instant.now();
  }
}
