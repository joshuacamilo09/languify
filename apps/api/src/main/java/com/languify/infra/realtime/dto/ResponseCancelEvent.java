package com.languify.infra.realtime.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseCancelEvent(@JsonProperty("response_id") String responseId)
    implements RealtimeClientEvent {
  public String type() {
    return "response.cancel";
  }
}
