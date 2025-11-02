package io.languify.communication.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
  private Long chatId;
  private String originalText;
  private String translatedText;
  private Direction direction;
}
