package io.languify.communication.conversation.service;

import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.repository.ConversationRepository;
import io.languify.identity.user.model.User;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService {
  private final ConversationRepository repository;

  public Conversation createConversation(String sourceLanguage, String targetLanguage, User user) {
    Conversation conversation = new Conversation();

    conversation.setToLanguage(sourceLanguage);
    conversation.setFromLanguage(targetLanguage);
    conversation.setUser(user);

    return this.repository.save(conversation);
  }

  public void deleteConversation(UUID userId, UUID conversationId) throws Exception {
    Optional<Conversation> optionalConversation =
        this.repository.findConversationById(conversationId);

    if (optionalConversation.isEmpty()) throw new Exception("Conversation not found");

    Conversation conversation = optionalConversation.get();

    if (!Objects.equals(userId, conversation.getUser().getId()))
      throw new Exception("User is not allowed to delete conversation");

    this.repository.delete(conversation);
  }
}
