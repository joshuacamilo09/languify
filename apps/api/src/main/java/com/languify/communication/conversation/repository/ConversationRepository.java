package com.languify.communication.conversation.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.languify.communication.conversation.model.Conversation;
import com.languify.communication.conversation.model.ConversationState;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
  boolean existsByAuthor_IdAndState(UUID userId, ConversationState state);
}
