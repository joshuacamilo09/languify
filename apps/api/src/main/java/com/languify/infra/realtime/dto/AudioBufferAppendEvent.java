package com.languify.infra.realtime.dto;

public record AudioBufferAppendEvent(String type, String audio) implements RealtimeClientEvent {
    public AudioBufferAppendEvent(String audio) {
        this("input_audio_buffer.append", audio);
    }
}
