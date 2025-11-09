package io.languify.communication.conversation.service;

import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.repository.ConversationRepository;
import io.languify.identity.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService {
  private final ConversationRepository repository;

  public Conversation createConversation(String sourceLanguage, String targetLanguage, User user) {
    Conversation conversation = new Conversation();

    conversation.setSourceLanguage(sourceLanguage);
    conversation.setTargetLanguage(targetLanguage);
    conversation.setUser(user);

    return this.repository.save(conversation);
  }
}
