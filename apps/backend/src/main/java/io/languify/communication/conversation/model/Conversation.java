package io.languify.communication.conversation.model;

import io.languify.identity.user.model.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "conversations")
@Data
public class Conversation {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String title;

  private String summary;

  @Column(nullable = false)
  private String fromLanguage;

  @Column(nullable = false)
  private String toLanguage;

  @Column(nullable = false)
  private LocalDate createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDate.now();
  }
}
