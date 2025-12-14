package com.languify.communication.conversation.service;

import org.springframework.stereotype.Service;
import com.languify.communication.conversation.model.Conversation;
import com.languify.communication.conversation.model.ConversationMessage;
import com.languify.communication.conversation.model.MessageSpeaker;
import com.languify.communication.conversation.repository.ConversationMessageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConversationMessageService {
  private final ConversationMessageRepository repository;

  public ConversationMessage create(Conversation conversation, String transcription,
      String translation, String sourceLanguage, String targetLanguage, MessageSpeaker speaker,
      String openaiResponseId) {
    ConversationMessage message = new ConversationMessage();
    message.setConversation(conversation);
    message.setTranscription(transcription);
    message.setTranslation(translation);
    message.setSourceLanguage(sourceLanguage);
    message.setTargetLanguage(targetLanguage);
    message.setSpeaker(speaker);
    message.setOpenaiResponseId(openaiResponseId);

    return repository.save(message);
  }
}
