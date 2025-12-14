package com.languify.conversation.domain.model

sealed class ConversationState {
    data object Idle : ConversationState()

    data object Initializing : ConversationState()

    data object AwaitingSourceLanguage : ConversationState()

    data class LanguageDetected(val language: String) : ConversationState()

    data object Recording : ConversationState()

    data object Processing : ConversationState()

    data object Playing : ConversationState()

    data class Error(val message: String) : ConversationState()
}

data class ConversationMessage(
    val transcription: String,
    val translation: String,
    val fromMe: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
)
