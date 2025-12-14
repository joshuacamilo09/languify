package com.languify.communication.conversation.dto;

public sealed
interface ConversationServerEvent
permits ConversationInitializingEvent,
    ConnectionInitializedEvent, ConnectionInitializationFailedEvent, LanguageDetectedEvent,
    TranscriptionDeltaEvent, TranslationDeltaEvent, AudioChunkEvent, ResponseCompleteEvent,
    ErrorOccurredEvent
{

  String type();
}
