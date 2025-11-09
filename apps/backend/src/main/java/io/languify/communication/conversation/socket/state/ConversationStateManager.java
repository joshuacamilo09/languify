package io.languify.communication.conversation.socket.state;

import io.languify.communication.conversation.model.Conversation;
import io.languify.infra.realtime.Realtime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ConversationStateManager {
  private final Map<String, ConversationState> states = new ConcurrentHashMap<>();

  public Optional<ConversationState> get(String userId) {
    return Optional.ofNullable(this.states.get(userId));
  }

  public boolean hasActiveConversation(String userId) {
    return this.states.containsKey(userId);
  }

  public void store(
      String userId, Conversation conversation, Realtime realtime, WebSocketSession session) {
    this.states.put(userId, new ConversationState(conversation, realtime, session));
  }

  public void remove(String userId) {

    this.states.remove(userId);
  }
}
