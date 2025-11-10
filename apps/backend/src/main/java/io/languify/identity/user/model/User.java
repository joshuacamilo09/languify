package io.languify.identity.user.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(columnDefinition = "UUID")
  private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column private String firstName;

  @Column private String lastName;

  @Column private String image;

  @Column private LocalDate createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDate.now();
  }
}
