package com.languify.communication.conversation.controller.websocket;

import com.languify.communication.conversation.model.Conversation;
import com.languify.infra.realtime.Realtime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ConversationStateManager {
  private final Map<UUID, ConversationState> states =
      new ConcurrentHashMap<>();

  public Optional<ConversationState> get(UUID userId) {
    return Optional.ofNullable(this.states.get(userId));
  }

  public void store(
      UUID userId,
      Conversation conversation,
      Realtime realtime,
      WebSocketSession session,
      String fromLanguage,
      String toLanguage) {
    this.states.put(
        userId,
        new ConversationState(conversation, realtime, session, fromLanguage, toLanguage)
    );
  }

  public void remove(UUID userId) {
    this.states.remove(userId);
  }

  public boolean hasActiveConversation(UUID userId) {
    return this.states.containsKey(userId);
  }
}
