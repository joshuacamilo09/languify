package com.languify.communication.conversation.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.languify.communication.conversation.model.ConversationMessage;

public interface ConversationMessageRepository extends JpaRepository<ConversationMessage, UUID> {
  List<ConversationMessage> findByConversation_IdOrderByCreatedAtAsc(UUID conversationId);
}
