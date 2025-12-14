package com.languify.infra.realtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SessionCreatedEvent(String type, Session session) implements RealtimeServerEvent {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Session(String id, String object, String model,
            @JsonProperty("expires_at") Long expiresAt, List<String> modalities) {
    }
}
