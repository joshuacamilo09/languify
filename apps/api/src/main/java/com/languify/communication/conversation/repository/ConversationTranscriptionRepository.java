package com.languify.communication.conversation.repository;

import com.languify.communication.conversation.model.ConversationTranscription;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationTranscriptionRepository
    extends JpaRepository<ConversationTranscription, UUID> {
  List<ConversationTranscription> findConversationTranscriptionsByConversationId(
      UUID conversationId);
}
