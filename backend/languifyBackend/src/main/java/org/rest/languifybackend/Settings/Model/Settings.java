package org.rest.languifybackend.Settings.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.rest.languifybackend.User.Model.User;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Settings")
public class Settings
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Boolean darkMode;

    @OneToOne
    private User userid;

    @Column
    private VoiceType voiceType;
}
