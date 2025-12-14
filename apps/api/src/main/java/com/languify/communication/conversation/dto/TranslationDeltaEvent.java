package com.languify.communication.conversation.dto;

public record TranslationDeltaEvent(String type, String delta, String full)
    implements ConversationServerEvent {
  public TranslationDeltaEvent(String delta, String full) {
    this("conversation.translation.delta", delta, full);
  }
}
