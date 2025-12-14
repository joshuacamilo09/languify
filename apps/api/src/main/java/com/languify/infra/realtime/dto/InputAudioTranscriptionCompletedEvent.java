package com.languify.infra.realtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InputAudioTranscriptionCompletedEvent(String type, String transcription, String itemId,
    String conversationId) implements RealtimeServerEvent {
}
