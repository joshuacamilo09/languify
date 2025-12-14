package com.languify.infra.realtime.service;

import java.util.function.Consumer;
import org.springframework.stereotype.Service;
import com.languify.infra.realtime.client.RealtimeClient;
import com.languify.infra.realtime.client.RealtimeClientParams;
import com.languify.infra.realtime.config.RealtimeConfig;
import com.languify.infra.realtime.config.RealtimeProperties;
import com.languify.infra.realtime.dto.RealtimeServerEvent;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RealtimeService {
  private final RealtimeProperties properties;

  public RealtimeClient createClient(RealtimeConfig config, Consumer<RealtimeServerEvent> handler,
      RealtimeClientParams params) {
    return new RealtimeClient(properties, config, handler, params);
  }
}
