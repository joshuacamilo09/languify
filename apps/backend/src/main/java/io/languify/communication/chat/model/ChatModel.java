package io.languify.communication.chat.model;

import io.languify.identity.user.model.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "chats")
@Data
public class ChatModel {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "UUID")
  private UUID id;

  @Column private String title;

  @Column private String summary;

  @Column(nullable = false)
  private LocalDate createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "userId", nullable = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  private User user;

  @PrePersist
  private void prePersist() {
    this.createdAt = LocalDate.now();
  }
}
