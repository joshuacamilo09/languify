package com.languify.infra.websocket.dto;

public record WebSocketMessage(String type, Object data) {
}
