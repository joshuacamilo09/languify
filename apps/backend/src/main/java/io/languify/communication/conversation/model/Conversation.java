package io.languify.communication.conversation.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "conversations")
public class Conversation {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private LocalDate createdAt;
}
