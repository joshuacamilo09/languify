package com.languify.infra.realtime.dto;

public sealed
interface RealtimeClientEvent
permits SessionUpdateEvent, AudioBufferAppendEvent,
    AudioBufferCommitEvent, ResponseCreateEvent, ResponseCancelEvent, InputAudioBufferClearEvent
{

  String type();
}
