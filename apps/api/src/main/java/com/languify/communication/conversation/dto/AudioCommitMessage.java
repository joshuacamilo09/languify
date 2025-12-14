package com.languify.communication.conversation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AudioCommitMessage(String type) implements ConversationClientMessage {
  public AudioCommitMessage() {
    this("audio.commit");
  }
}
