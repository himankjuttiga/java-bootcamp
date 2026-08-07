package com.northstar.crm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

// Enabled via @EnableConfigurationProperties(NorthstarIntegrationProperties.class) on CrmApplication
@ConfigurationProperties(prefix = "northstar.integration")
public class NorthstarIntegrationProperties {
  private String apiBaseUrl;
  private int connectTimeoutMs = 2000;
  private String apiKey;
  public String getApiBaseUrl() { return apiBaseUrl; }
  public void setApiBaseUrl(String apiBaseUrl) { this.apiBaseUrl = apiBaseUrl; }
  public int getConnectTimeoutMs() { return connectTimeoutMs; }
  public void setConnectTimeoutMs(int connectTimeoutMs) { this.connectTimeoutMs = connectTimeoutMs; }
  public String getApiKey() { return apiKey; }
  public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}
