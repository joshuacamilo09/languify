package io.languify.communication.conversation.socket.state;

import io.languify.communication.conversation.model.Conversation;
import io.languify.infra.realtime.Realtime;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class ConversationState {
  private final Conversation conversation;
  private final Realtime realtime;
  private final WebSocketSession session;

  private String fromLanguage;
  private String toLanguage;

  public ConversationState(
      Conversation conversation,
      Realtime realtime,
      WebSocketSession session,
      String fromLanguage,
      String toLanguage) {
    this.conversation = conversation;
    this.realtime = realtime;
    this.session = session;

    this.fromLanguage = fromLanguage;
    this.toLanguage = toLanguage;
  }

  public void swapLanguages() {
    String temp = this.fromLanguage;

    this.fromLanguage = this.toLanguage;
    this.toLanguage = temp;
  }
}
