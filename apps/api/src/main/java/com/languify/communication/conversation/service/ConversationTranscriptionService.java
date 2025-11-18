package com.languify.communication.conversation.service;

import com.languify.communication.conversation.model.Conversation;
import com.languify.communication.conversation.model.ConversationTranscription;
import com.languify.communication.conversation.repository.ConversationTranscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationTranscriptionService {
  private final ConversationTranscriptionRepository repository;

  public ConversationTranscription createConversationTranscription(Conversation conversation) {
    ConversationTranscription transcription = new ConversationTranscription();
    transcription.setConversation(conversation);

    return this.repository.save(transcription);
  }
}
