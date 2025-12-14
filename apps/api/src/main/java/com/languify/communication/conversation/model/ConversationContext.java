package com.languify.communication.conversation.model;

import java.util.Optional;
import com.languify.identity.auth.model.Session;
import com.languify.infra.realtime.client.RealtimeClient;
import lombok.Data;

@Data
public class ConversationContext {
  private Session session;
  private Optional<RealtimeClient> client;
  private Optional<Conversation> conversation;
  private TranslationState translationState = TranslationState.AWAITING_SOURCE_LANGUAGE;
  private StringBuilder currentTranscription = new StringBuilder();
  private StringBuilder currentTranslation = new StringBuilder();
  private String currentResponseId;
  private boolean audioBuffered = false;

  public ConversationContext(Session session) {
    this.session = session;
  }
}
