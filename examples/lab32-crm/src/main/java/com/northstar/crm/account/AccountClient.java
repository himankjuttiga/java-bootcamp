package com.northstar.crm.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.HttpServerErrorException;

@Component
public class AccountClient {

  private final RestClient restClient;

  public AccountClient(@Value("${account.api.base-url}") String baseUrl) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
  }

  public AccountSummary fetch(String customerId) {
    try {
      return restClient.get()
          .uri("/accounts/{customerId}/summary", customerId)
          .retrieve()
          .body(AccountSummary.class);
    } catch (HttpServerErrorException e) {
      // 5xx is transient -> retryable; anything else propagates as-is.
      throw new TemporaryAccountException(
          "account API returned " + e.getStatusCode() + " for " + customerId, e);
    }
  }
}
