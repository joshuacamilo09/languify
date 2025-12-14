package com.languify.identity.user.model;

import java.util.List;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.languify.communication.conversation.model.Conversation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column
  private UUID id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, unique = true)
  private String username;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Column
  private String password;

  @OneToMany(mappedBy = "author")
  private List<Conversation> conversations;
}
