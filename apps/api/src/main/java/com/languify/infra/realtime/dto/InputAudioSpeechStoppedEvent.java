package com.languify.infra.realtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InputAudioSpeechStoppedEvent(String type) implements RealtimeServerEvent {
}

