package com.languify.communication.conversation.dto;

public record AudioChunkEvent(String type, String audio) implements ConversationServerEvent {
  public AudioChunkEvent(String audio) {
    this("conversation.audio.chunk", audio);
  }
}
