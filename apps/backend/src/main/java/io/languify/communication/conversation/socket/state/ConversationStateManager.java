package io.languify.communication.conversation.socket.state;

import io.languify.communication.conversation.model.Conversation;
import io.languify.infra.realtime.Realtime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ConversationStateManager {
  private Map<String, ConversationState> states = new ConcurrentHashMap();

  public ConversationState get(String conversationId) {
    return this.states.get(conversationId);
  }

  public void store(Conversation conversation, Realtime realtime, WebSocketSession session) {
    this.states.put(conversation.getId(), new ConversationState(conversation, realtime, session));
  }
}
