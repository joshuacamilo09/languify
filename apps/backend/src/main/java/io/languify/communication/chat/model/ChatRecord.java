package io.languify.communication.chat.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "Chat_Record")
public class ChatRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column private String Originaltext;

  @Column private String Translatedtext;

  private LocalDateTime timestamp;

  @Enumerated(EnumType.STRING)
  private Direction direction;

  @ManyToOne private Chat chat;
}
