package org.rest.languifybackend.Chat.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "Chat_Record")
public class ChatRecord
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String Originaltext;

    private String Translatedtext;

    private LocalDateTime timestamp;

    private Direction direction;

    @ManyToOne
    private Chat chat;
}
