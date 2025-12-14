package com.languify.communication.conversation.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AudioChunkMessage(String type, String audio)
    implements ConversationClientMessage {
  public AudioChunkMessage(String audio) {
    this("audio.chunk", audio);
  }
}
