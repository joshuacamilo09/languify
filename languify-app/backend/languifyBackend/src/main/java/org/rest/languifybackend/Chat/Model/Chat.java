package org.rest.languifybackend.Chat.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rest.languifybackend.User.Model.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat
{
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long chat_id;

    @Column
    private LocalDateTime dateTime;

    @Column
    private String origin_Idiom;

    @Column
    private String Destination_Idiom;

    @ManyToOne
    private User user;

    @OneToMany
    private List<ChatRecord> chats;
}
