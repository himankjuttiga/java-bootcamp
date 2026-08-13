package com.northstar.crm.account;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountProfileService {

  private static final Logger log = LoggerFactory.getLogger(AccountProfileService.class);

  private final AccountClient client;

  public AccountProfileService(AccountClient client) {
    this.client = client;
  }

  // TimeLimiter needs an async (CompletableFuture) return so it can cancel on timeout.
  @CircuitBreaker(name = "accountProfile", fallbackMethod = "fallback")
  @Retry(name = "accountProfile")
  @TimeLimiter(name = "accountProfile")
  public CompletableFuture<AccountSummary> find(String customerId) {
    return CompletableFuture.supplyAsync(() -> client.fetch(customerId));
  }

  @SuppressWarnings("unused")
  private CompletableFuture<AccountSummary> fallback(String customerId, Throwable ex) {
    // Truthful degraded read: available=false. Never a fake success; never used for writes.
    log.warn("account_profile_degraded customerId={} cause={}",
        customerId, ex.getClass().getSimpleName());
    return CompletableFuture.completedFuture(AccountSummary.unavailable(customerId));
  }
}
