package io.languify.communication.conversation.service;

import io.languify.communication.conversation.model.Conversation;
import io.languify.communication.conversation.model.ConversationTranscription;
import io.languify.communication.conversation.repository.ConversationTranscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationTranscriptionService {
  private ConversationTranscriptionRepository repository;

  public ConversationTranscription createConversationTranscription(Conversation conversation) {
    ConversationTranscription transcription = new ConversationTranscription();
    transcription.setConversation(conversation);

    return this.repository.save(transcription);
  }
}
