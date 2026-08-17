package com.northstar.crm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Lab 35 — development CORS for the React CRM SPA.
 *
 * Copy into the Spring CRM project as
 * src/main/java/com/northstar/crm/config/WebConfig.java and restart Spring.
 *
 * The allowlist is explicit on purpose: "*" cannot be used with credentials and would let
 * any page in a logged-in browser read CRM responses. Keep one entry per environment.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  private static final String VITE_DEV_ORIGIN = "http://localhost:5173";

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/api/**")
        .allowedOrigins(VITE_DEV_ORIGIN)
        .allowedMethods("GET", "POST", "PUT", "DELETE")
        // X-Correlation-Id must be listed or the preflight fails before the real request.
        .allowedHeaders("Content-Type", "Authorization", "X-Correlation-Id")
        .maxAge(3600);
  }
}
