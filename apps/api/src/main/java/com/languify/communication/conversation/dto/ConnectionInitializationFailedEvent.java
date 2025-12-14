package com.languify.communication.conversation.dto;

public record ConnectionInitializationFailedEvent(String type) implements ConversationServerEvent {
    public ConnectionInitializationFailedEvent() {
        this("connection.initialization.failed");
    }
}
