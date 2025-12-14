package com.languify.communication.conversation.dto;

public record TranscriptionDeltaEvent(String type, String delta, String full)
    implements ConversationServerEvent {
  public TranscriptionDeltaEvent(String delta, String full) {
    this("conversation.transcription.delta", delta, full);
  }
}
