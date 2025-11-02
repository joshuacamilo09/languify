package io.languify.platform.settings.model;

import io.languify.identity.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Settings")
public class Settings {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column private Boolean darkMode;

  @OneToOne private User userid;

  @Column private VoiceType voiceType;
}
