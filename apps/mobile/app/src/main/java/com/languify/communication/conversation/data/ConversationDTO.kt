package com.languify.communication.conversation.data

data class StartConversationDTO(val fromLanguage: String, val toLanguage: String)

data class ProcessConversationDataDTO(val audio: String)

data class TranslationStateDTO(val state: String)

data class AudioDeltaDTO(val audio: String)
