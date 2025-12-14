package com.languify.communication.conversation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InterruptMessage(String type) implements ConversationClientMessage {
  public InterruptMessage() {
    this("interrupt");
  }
}
