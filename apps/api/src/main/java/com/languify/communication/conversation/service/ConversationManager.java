package com.languify.communication.conversation.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import com.languify.communication.conversation.model.Conversation;
import com.languify.communication.conversation.model.ConversationContext;
import com.languify.identity.auth.model.Session;
import com.languify.infra.realtime.client.RealtimeClient;

@Service
public class ConversationManager {
  private final ConcurrentHashMap<UUID, ConversationContext> contexts = new ConcurrentHashMap<>();

  public ConversationContext createContext(Session session) {
    return new ConversationContext(session);
  }

  public Optional<ConversationContext> getContext(UUID userId) {
    ConversationContext context = contexts.get(userId);
    if (context == null)
      return Optional.empty();

    return Optional.of(context);
  }

  public void addContext(ConversationContext context, UUID userId) {
    contexts.put(userId, context);
  }

  public void setContextConversation(Conversation conversation, UUID userId) {
    contexts.computeIfPresent(userId, (_, existing) -> {
      existing.setConversation(Optional.of(conversation));
      return existing;
    });
  }

  public void setContextRealtimeClient(RealtimeClient client, UUID userId) {
    contexts.computeIfPresent(userId, (_, existing) -> {
      existing.setClient(Optional.of(client));
      return existing;
    });
  }

  public void removeContext(UUID userId) {
    contexts.remove(userId);
  }
}
