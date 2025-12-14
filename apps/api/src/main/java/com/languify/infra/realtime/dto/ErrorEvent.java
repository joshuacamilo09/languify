package com.languify.infra.realtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorEvent(String type, Error error) implements RealtimeServerEvent {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Error(String type, String code, String message, String param) {
    }
}
