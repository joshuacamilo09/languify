package com.languify.communication.conversation.domain

enum class TranslationState {
  IDLE,
  LOADING,
  REPRODUCING,
  DONE,
  READY,
}

enum class RecordingState {
  IDLE,
  RECORDING,
  PROCESSING,
}

data class ActiveConversation(
  val fromLanguage: String,
  val toLanguage: String,
  val recordingState: RecordingState = RecordingState.IDLE,
  val translationState: TranslationState = TranslationState.IDLE,
)
