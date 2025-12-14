package com.languify.communication.conversation.dto;

public record ConnectionInitializedEvent(String type) implements ConversationServerEvent {
    public ConnectionInitializedEvent() {
        this("connection.initialized");
    }
}
