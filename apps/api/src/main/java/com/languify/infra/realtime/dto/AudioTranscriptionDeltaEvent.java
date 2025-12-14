package com.languify.infra.realtime.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AudioTranscriptionDeltaEvent(String type,
                @JsonProperty("response_id") String responseId,
                @JsonProperty("item_id") String itemId,
                @JsonProperty("output_index") Integer outputIndex,
                @JsonProperty("content_index") Integer contentIndex, String delta,
                String transcript)
                implements RealtimeServerEvent {
}
