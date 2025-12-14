package com.languify.communication.conversation.dto;

public record ResponseCompleteEvent(String type, String responseId)
    implements ConversationServerEvent {
  public ResponseCompleteEvent(String responseId) {
    this("conversation.response.complete", responseId);
  }
}
