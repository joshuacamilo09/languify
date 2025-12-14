package com.languify.infra.realtime.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "realtime")
@Data
public class RealtimeProperties {
  private String url;
  private String secret;
  private String model;
}
