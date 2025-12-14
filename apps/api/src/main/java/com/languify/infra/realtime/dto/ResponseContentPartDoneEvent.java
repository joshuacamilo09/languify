package com.languify.infra.realtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ResponseContentPartDoneEvent(String type) implements RealtimeServerEvent {
}

