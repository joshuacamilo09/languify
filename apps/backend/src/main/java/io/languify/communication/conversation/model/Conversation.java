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

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private LocalDate createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @PrePersist
  public void persist() {
    this.createdAt = LocalDate.now();
  }
}
