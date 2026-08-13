package com.northstar.crm.account;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.lessThan;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountProfileResilienceTest {

  private static WireMockServer wireMock;

  @Autowired
  AccountProfileService service;

  @Autowired
  CircuitBreakerRegistry circuitBreakerRegistry;

  @BeforeAll
  static void startWireMock() {
    wireMock = new WireMockServer(8089);
    wireMock.start();
    WireMock.configureFor("localhost", 8089);
  }

  @AfterAll
  static void stopWireMock() {
    wireMock.stop();
  }

  @BeforeEach
  void reset() {
    wireMock.resetAll();
    circuitBreakerRegistry.circuitBreaker("accountProfile").reset();
  }

  @Test
  void healthyDependencyReturnsAvailable() throws Exception {
    stubFor(get(urlEqualTo("/accounts/CUS-1001/summary"))
        .willReturn(okJson("{\"customerId\":\"CUS-1001\",\"available\":true,\"note\":\"ok\"}")));

    AccountSummary summary = service.find("CUS-1001").get(5, TimeUnit.SECONDS);

    assertThat(summary.available()).isTrue();
    assertThat(summary.customerId()).isEqualTo("CUS-1001");
  }

  @Test
  void failingDependencyFallsBackUnavailable() throws Exception {
    stubFor(get(urlEqualTo("/accounts/CUS-1001/summary"))
        .willReturn(aResponse().withStatus(503)));

    AccountSummary summary = service.find("CUS-1001").get(5, TimeUnit.SECONDS);

    assertThat(summary.available()).isFalse();
    assertThat(summary.note()).isEqualTo("account-profile-unavailable");
  }

  @Test
  void slowDependencyTimesOutToFallback() throws Exception {
    stubFor(get(urlEqualTo("/accounts/CUS-1001/summary"))
        .willReturn(okJson("{\"customerId\":\"CUS-1001\",\"available\":true,\"note\":\"ok\"}")
            .withFixedDelay(3000)));

    long start = System.currentTimeMillis();
    AccountSummary summary = service.find("CUS-1001").get(10, TimeUnit.SECONDS);
    long elapsedMs = System.currentTimeMillis() - start;

    assertThat(summary.available()).isFalse();         // timed out -> honest fallback
    assertThat(elapsedMs).isLessThan(2800);            // ~1.5s budget, not the 3s hang
  }

  @Test
  void circuitOpensAndFailsFast() throws Exception {
    stubFor(get(urlEqualTo("/accounts/CUS-1001/summary"))
        .willReturn(aResponse().withStatus(503)));

    for (int i = 0; i < 8; i++) {
      service.find("CUS-1001").get(5, TimeUnit.SECONDS); // each returns the fallback
    }

    CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("accountProfile");
    assertThat(cb.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    // Once OPEN, calls short-circuit -> WireMock received fewer requests than calls made.
    verify(lessThan(8), getRequestedFor(urlEqualTo("/accounts/CUS-1001/summary")));
  }
}
