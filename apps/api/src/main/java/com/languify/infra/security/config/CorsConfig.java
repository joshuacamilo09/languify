package com.languify.infra.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

  @Value("${cors.allowed.origins}")
  private String allowedOrigins;

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Parse allowed origins from comma-separated string
    List<String> origins = Arrays.asList(allowedOrigins.split(","));
    configuration.setAllowedOrigins(origins);

    // Allow common HTTP methods
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

    // Allow common headers
    configuration.setAllowedHeaders(Arrays.asList("*"));

    // Allow credentials (cookies, authorization headers)
    configuration.setAllowCredentials(true);

    // How long the response from a pre-flight request can be cached
    configuration.setMaxAge(3600L);

    // Expose headers that the client can access
    configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  public String[] getAllowedOriginsArray() {
    return allowedOrigins.split(",");
  }
}
