package io.languify.communication.conversation.handler;

import io.languify.identity.auth.model.Session;
import io.languify.infra.socket.skeletons.HandlerSkeleton;

public class ConversationHandler extends HandlerSkeleton {
  public ConversationHandler(Session session) {
    super(session);
  }

  public void handleSegment(String segment) {}
}
