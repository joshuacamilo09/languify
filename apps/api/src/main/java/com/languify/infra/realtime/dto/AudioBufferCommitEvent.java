package com.languify.infra.realtime.dto;

public record AudioBufferCommitEvent(String type) implements RealtimeClientEvent {
    public AudioBufferCommitEvent() {
        this("input_audio_buffer.commit");
    }
}
