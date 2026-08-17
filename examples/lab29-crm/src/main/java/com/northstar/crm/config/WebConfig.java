package com.northstar.crm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Lab 35 — development CORS for the React CRM SPA (Vite on :5173).
 *
 * Spring Security consumes this bean through http.cors(...), so the allowlist applies to the
 * security filter chain and not only to MVC handlers.
 *
 * The allowlist is explicit on purpose: "*" cannot be combined with credentials and would let
 * any page in a logged-in browser read CRM responses. One entry per environment.
 */
@Configuration
public class WebConfig {

  private static final String VITE_DEV_ORIGIN = "http://localhost:5173";

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(VITE_DEV_ORIGIN));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    // X-Correlation-Id must be listed or the preflight fails before the real request is sent.
    config.setAllowedHeaders(List.of("Content-Type", "Authorization", "X-Correlation-Id"));
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
  }
}
