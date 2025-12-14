package com.languify.communication.conversation.dto;

import com.languify.communication.conversation.model.ConversationState;

public record ConversationUpdate(String title, ConversationState state, String sourceLanguage,
    String targetLanguage) {
}
