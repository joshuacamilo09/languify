package com.languify.infra.realtime.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.languify.infra.realtime.config.RealtimeConfig;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SessionUpdateEvent(String type, RealtimeConfig session)
        implements RealtimeClientEvent {
    public SessionUpdateEvent(RealtimeConfig session) {
        this("session.update", session);
    }
}
