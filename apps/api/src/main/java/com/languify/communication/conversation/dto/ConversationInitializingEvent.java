package com.languify.communication.conversation.dto;

public record ConversationInitializingEvent(String type) implements ConversationServerEvent {
    public ConversationInitializingEvent() {
        this("conversation.initializing");
    }
}
