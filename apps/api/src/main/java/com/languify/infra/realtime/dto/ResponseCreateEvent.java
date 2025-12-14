package com.languify.infra.realtime.dto;

public record ResponseCreateEvent(String type) implements RealtimeClientEvent {
    public ResponseCreateEvent() {
        this("response.create");
    }
}
