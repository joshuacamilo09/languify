package com.languify.infra.realtime.data.model

sealed class RealtimeEvent {
    data object ConversationInitializing : RealtimeEvent()

    data object ConnectionInitialized : RealtimeEvent()

    data class ConnectionInitializationFailed(
        val reason: String? = null,
    ) : RealtimeEvent()

    data class LanguageDetected(val language: String) : RealtimeEvent()

    data class TranscriptionDelta(val delta: String, val full: String) : RealtimeEvent()

    data class TranslationDelta(val delta: String, val full: String) : RealtimeEvent()

    data class AudioChunk(val audio: String) : RealtimeEvent()

    data class ResponseComplete(val responseId: String) : RealtimeEvent()

    data class ErrorOccurred(val error: String, val message: String) : RealtimeEvent()

    data class UnknownEvent(
        val type: String,
        val message: RealtimeMessage,
    ) : RealtimeEvent()

    companion object {
        const val TYPE_CONVERSATION_INITIALIZING = "conversation.initializing"
        const val TYPE_CONNECTION_INITIALIZED = "connection.initialized"
        const val TYPE_CONNECTION_INITIALIZATION_FAILED = "connection.initialization.failed"
        const val TYPE_LANGUAGE_DETECTED = "conversation.language.detected"
        const val TYPE_TRANSCRIPTION_DELTA = "conversation.transcription.delta"
        const val TYPE_TRANSLATION_DELTA = "conversation.translation.delta"
        const val TYPE_AUDIO_CHUNK = "conversation.audio.chunk"
        const val TYPE_RESPONSE_COMPLETE = "conversation.response.complete"
        const val TYPE_ERROR_OCCURRED = "conversation.error"

        fun fromMessage(message: RealtimeMessage): RealtimeEvent =
            when (message.type) {
                TYPE_CONVERSATION_INITIALIZING -> ConversationInitializing
                TYPE_CONNECTION_INITIALIZED -> ConnectionInitialized
                TYPE_CONNECTION_INITIALIZATION_FAILED -> ConnectionInitializationFailed()
                TYPE_LANGUAGE_DETECTED -> {
                    val language =
                        message.data?.asJsonObject?.get("language")?.asString ?: "unknown"
                    LanguageDetected(language)
                }

                TYPE_TRANSCRIPTION_DELTA -> {
                    val obj = message.data?.asJsonObject
                    TranscriptionDelta(
                        obj?.get("delta")?.asString ?: "",
                        obj?.get("full")?.asString ?: "",
                    )
                }

                TYPE_TRANSLATION_DELTA -> {
                    val obj = message.data?.asJsonObject
                    TranslationDelta(
                        obj?.get("delta")?.asString ?: "",
                        obj?.get("full")?.asString ?: "",
                    )
                }

                TYPE_AUDIO_CHUNK -> {
                    val audio = message.data?.asJsonObject?.get("audio")?.asString ?: ""
                    AudioChunk(audio)
                }

                TYPE_RESPONSE_COMPLETE -> {
                    val id = message.data?.asJsonObject?.get("responseId")?.asString ?: ""
                    ResponseComplete(id)
                }

                TYPE_ERROR_OCCURRED -> {
                    val obj = message.data?.asJsonObject
                    ErrorOccurred(
                        obj?.get("error")?.asString ?: "unknown",
                        obj?.get("message")?.asString ?: "",
                    )
                }

                else -> UnknownEvent(message.type, message)
            }
    }
}
