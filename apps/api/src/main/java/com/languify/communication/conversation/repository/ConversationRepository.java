package com.languify.communication.conversation.repository;

import com.languify.communication.conversation.model.Conversation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
  Optional<Conversation> findConversationById(UUID id);

  List<Conversation> findConversationsByUserId(UUID userId);
}
