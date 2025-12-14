package com.languify.infra.realtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AudioTranscriptionDoneEvent(String type,
                @JsonProperty("response_id") String responseId) implements RealtimeServerEvent {
}

