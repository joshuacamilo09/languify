package com.languify.infra.websocket.service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class WebSocketManager {
  private final ConcurrentHashMap<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();

  public Optional<WebSocketSession> getSession(UUID userId) {
    return Optional.of(sessions.get(userId));
  }

  public void addSession(WebSocketSession session, UUID userId) {
    sessions.put(userId, session);
  }

  public void removeSession(UUID userId) {
    sessions.remove(userId);
  }
}
