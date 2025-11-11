package io.languify.communication.conversation.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "conversation_transcriptions")
@Data
public class ConversationTranscription {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "UUID")
  private UUID id;

  @Column private String originalTranscript;

  @Column private String translatedTranscript;

  @Column(nullable = false)
  private LocalDate createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Conversation conversation;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDate.now();
  }
}
