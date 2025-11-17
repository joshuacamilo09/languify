package io.languify.communication.conversation.model;

import io.languify.identity.user.model.User;
import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "conversations")
@Data
public class Conversation {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "UUID")
  private UUID id;

  @Column private String title;

  @Column private String summary;

  @Column(nullable = false)
  private String fromLanguage;

  @Column(nullable = false)
  private String toLanguage;

  @Column(nullable = false)
  private Instant createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @PrePersist
  public void prePersist() {
    this.createdAt = Instant.now();
  }
}
