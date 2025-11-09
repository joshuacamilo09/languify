package io.languify.communication.conversation.service;

import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.repo.ConversationRepository;
import io.languify.identity.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService {
  private final ConversationRepository repository;

  public Conversation createConversation(String title, User user) {
    Conversation conversation = new Conversation();

    conversation.setTitle(title);
    conversation.setUser(user);

    return this.repository.save(conversation);
  }
}
