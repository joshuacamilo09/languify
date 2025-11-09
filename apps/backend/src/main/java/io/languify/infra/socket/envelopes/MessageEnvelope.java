package io.languify.infra.socket.envelopes;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class MessageEnvelope {
  private String event;
  private JsonNode data;
}
