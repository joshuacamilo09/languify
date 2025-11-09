package io.languify.infra.websocket.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class WebSocketMessageDTO {
  private String event;
  private JsonNode data;
}
