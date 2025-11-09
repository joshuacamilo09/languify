package io.languify.identity.user.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, unique = true)
  private String email;

  private String firstName;

  private String lastName;

  private String image;

  private LocalDate createdAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDate.now();
  }
}
