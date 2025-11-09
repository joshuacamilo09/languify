package io.languify.communication.conversation.repository;

import io.languify.communication.conversation.model.Conversation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, String> {
  List<Conversation> findConversationsByUserId(String userId);
}
