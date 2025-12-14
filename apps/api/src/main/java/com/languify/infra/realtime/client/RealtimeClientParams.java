package com.languify.infra.realtime.client;

public interface RealtimeClientParams {
  void onConnect(RealtimeClient client);

  void onDisconnect(RealtimeClient client);
}
