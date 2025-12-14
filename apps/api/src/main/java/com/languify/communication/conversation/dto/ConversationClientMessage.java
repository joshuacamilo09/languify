package com.languify.communication.conversation.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({@JsonSubTypes.Type(value = AudioChunkMessage.class, name = "audio.chunk"),
    @JsonSubTypes.Type(value = AudioCommitMessage.class, name = "audio.commit"),
    @JsonSubTypes.Type(value = InterruptMessage.class, name = "interrupt"),
    @JsonSubTypes.Type(value = CloseConversationMessage.class, name = "close")})
public interface ConversationClientMessage {
  String type();
}
