package com.languify.infra.realtime.dto;

public record InputAudioBufferClearEvent() implements RealtimeClientEvent {
  public String type() {
    return "input_audio_buffer.clear";
  }
}
