package com.languify.communication.conversation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CloseConversationMessage(String type) implements ConversationClientMessage {
  public CloseConversationMessage() {
    this("close");
  }
}
