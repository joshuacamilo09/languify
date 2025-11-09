package io.languify.infra.socket.skeletons;

import io.languify.identity.auth.model.Session;

public abstract class HandlerSkeleton {
  protected Session session;

  public HandlerSkeleton(Session session) {
    this.session = session;
  }

  public abstract void handleSegment(String segment);
}
