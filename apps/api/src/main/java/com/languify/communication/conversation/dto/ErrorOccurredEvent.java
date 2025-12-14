package com.languify.communication.conversation.dto;

public record ErrorOccurredEvent(String type, String error, String message)
    implements ConversationServerEvent {
  public ErrorOccurredEvent(String error, String message) {
    this("conversation.error", error, message);
  }
}
