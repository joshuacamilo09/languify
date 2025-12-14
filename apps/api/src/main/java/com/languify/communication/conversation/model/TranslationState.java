package com.languify.communication.conversation.model;

public enum TranslationState {
  AWAITING_SOURCE_LANGUAGE, // Waiting for first user speech
  LANGUAGE_DETECTED, // Language detected, updating session
  ACTIVE_TRANSLATION, // Ready for translation
  PROCESSING_AUDIO, // Waiting for OpenAI response
  PLAYING_AUDIO, // Audio playing on mobile
  INTERRUPTED, // User spoke during playback
  CLOSING // Cleanup in progress
}
