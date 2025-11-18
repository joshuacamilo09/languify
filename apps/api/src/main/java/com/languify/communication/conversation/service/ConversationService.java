package com.languify.communication.conversation.service;

import com.languify.communication.conversation.model.Conversation;
import com.languify.communication.conversation.repository.ConversationRepository;
import com.languify.identity.user.model.User;
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
}
