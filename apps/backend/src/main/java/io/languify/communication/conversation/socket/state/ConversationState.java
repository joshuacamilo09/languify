package io.languify.communication.conversation.socket.state;

import io.languify.communication.conversation.model.Conversation;
import io.languify.infra.realtime.Realtime;
import org.springframework.web.socket.WebSocketSession;

public class ConversationState {
  private Conversation conversation;
  private Realtime realtime;
  private WebSocketSession session;

  public ConversationState(Conversation conversation, Realtime realtime, WebSocketSession session) {
    this.conversation = conversation;
    this.realtime = realtime;
    this.session = session;
  }
}
