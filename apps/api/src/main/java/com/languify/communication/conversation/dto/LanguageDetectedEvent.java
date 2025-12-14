package com.languify.communication.conversation.dto;

public record LanguageDetectedEvent(String type, String language)
    implements ConversationServerEvent {
  public LanguageDetectedEvent(String language) {
    this("conversation.language.detected", language);
  }
}
