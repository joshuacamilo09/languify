package com.languify.infra.realtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InputAudioTranscriptionDeltaEvent(String type, String delta, String itemId,
    String conversationId) implements RealtimeServerEvent {
}
