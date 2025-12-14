package com.languify.communication.conversation.service;

import java.util.UUID;
import org.springframework.stereotype.Service;
import com.languify.communication.conversation.dto.ConversationUpdate;
import com.languify.communication.conversation.model.Conversation;
import com.languify.communication.conversation.model.ConversationState;
import com.languify.communication.conversation.repository.ConversationRepository;
import com.languify.identity.user.model.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationService {
  private final ConversationRepository conversationRepository;

  public boolean existsActive(UUID userId) {
    return conversationRepository.existsByAuthor_IdAndState(userId, ConversationState.ACTIVE);
  }

  public Conversation create(User author) throws Exception {
    if (existsActive(author.getId()))
      throw new Exception(
          "Trying to create a conversation for author with an active conversation.");

    Conversation conversation = new Conversation();

    conversation.setTitle("");
    conversation.setState(ConversationState.INITIALIZING);
    conversation.setAuthor(author);

    return conversationRepository.save(conversation);
  }

  public Conversation update(ConversationUpdate data, UUID id) throws Exception {
    Conversation conversation = conversationRepository.findById(id)
        .orElseThrow(() -> new Exception("Conversation not found."));

    if (data.title() != null && !data.title().equals(conversation.getTitle()))
      conversation.setTitle(data.title());

    if (data.state() != null && !data.state().equals(conversation.getState()))
      conversation.setState(data.state());

    if (data.sourceLanguage() != null
        && !data.sourceLanguage().equals(conversation.getSourceLanguage()))
      conversation.setSourceLanguage(data.sourceLanguage());

    if (data.targetLanguage() != null
        && !data.targetLanguage().equals(conversation.getTargetLanguage()))
      conversation.setTargetLanguage(data.targetLanguage());

    return conversationRepository.save(conversation);
  }
}
