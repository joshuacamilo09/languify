package com.languify.infra.realtime.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = SessionCreatedEvent.class, name = "session.created"),
    @JsonSubTypes.Type(value = SessionUpdatedEvent.class, name = "session.updated"),
    @JsonSubTypes.Type(value = InputAudioSpeechStartedEvent.class,
        name = "input_audio_buffer.speech_started"),
    @JsonSubTypes.Type(value = InputAudioSpeechStoppedEvent.class,
        name = "input_audio_buffer.speech_stopped"),
    @JsonSubTypes.Type(value = InputAudioCommittedEvent.class,
        name = "input_audio_buffer.committed"),
    @JsonSubTypes.Type(value = ResponseCreatedEvent.class, name = "response.created"),
    @JsonSubTypes.Type(value = ResponseOutputItemAddedEvent.class,
        name = "response.output_item.added"),
    @JsonSubTypes.Type(value = ResponseOutputItemDoneEvent.class,
        name = "response.output_item.done"),
    @JsonSubTypes.Type(value = ConversationItemCreatedEvent.class,
        name = "conversation.item.created"),
    @JsonSubTypes.Type(value = ResponseContentPartAddedEvent.class,
        name = "response.content_part.added"),
    @JsonSubTypes.Type(value = ResponseContentPartDoneEvent.class,
        name = "response.content_part.done"),
    @JsonSubTypes.Type(value = AudioTranscriptionDeltaEvent.class,
        name = "response.audio_transcript.delta"),
    @JsonSubTypes.Type(value = AudioTranscriptionDoneEvent.class,
        name = "response.audio_transcript.done"),
    @JsonSubTypes.Type(value = TextDeltaEvent.class, name = "response.text.delta"),
    @JsonSubTypes.Type(value = AudioDeltaEvent.class, name = "response.audio.delta"),
    @JsonSubTypes.Type(value = AudioDoneEvent.class, name = "response.audio.done"),
    @JsonSubTypes.Type(value = ResponseDoneEvent.class, name = "response.done"),
    @JsonSubTypes.Type(value = InputAudioTranscriptionDeltaEvent.class,
        name = "conversation.item.input_audio_transcription.delta"),
    @JsonSubTypes.Type(value = InputAudioTranscriptionCompletedEvent.class,
        name = "conversation.item.input_audio_transcription.completed"),
    @JsonSubTypes.Type(value = ErrorEvent.class, name = "error")})

public interface RealtimeServerEvent {

  String type();
}
