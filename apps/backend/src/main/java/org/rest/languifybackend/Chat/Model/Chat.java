package org.rest.languifybackend.Chat.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rest.languifybackend.User.Model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private LocalDateTime created_at = LocalDateTime.now();

    @Column
    private String origin_Idiom;

    @Column
    private String Destination_Idiom;

    @ManyToOne
    @JoinColumn(name = "user1_id",  nullable = false)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id",  nullable = false)
    private User user2;

    @OneToOne
    private Location location;

    @OneToMany(mappedBy = "chat",  cascade = CascadeType.ALL)
    private List<ChatRecord> messages;

    @Column(nullable = false)
    private boolean active = true;
}
