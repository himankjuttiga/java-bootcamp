# Lab 32 — Fill Resilience TODOs

## Step 1 — Filled annotation snippet

```java
@CircuitBreaker(name = "accountProfile", fallbackMethod = "profileFallback")
@Retry(name = "accountProfile")
@TimeLimiter(name = "accountProfile")
public CompletableFuture<AccountProfile> getProfile(String customerId) {
  return accountClient.fetch(customerId); // remote client
}

private CompletableFuture<AccountProfile> profileFallback(String customerId, Throwable t) {
  // log correlationId lab-request-001 when the fallback fires
  // return a minimal, honest degraded profile for CUS-1001 / CUS-1002
  return CompletableFuture.completedFuture(AccountProfile.minimal(customerId));
}
```

## Step 2 — Fills

- CircuitBreaker name -> `accountProfile`
- fallbackMethod -> `profileFallback`
- remote client -> `accountClient`
- fallback return -> `AccountProfile.minimal(customerId)` (available=false)

## Step 3 — Config numbers (YAML)

```yaml
resilience4j:
  circuitbreaker:
    instances:
      accountProfile:
        failureRateThreshold: 50          # % failures in the window to open
        slidingWindowSize: 10
        waitDurationInOpenState: 10s       # stay open before half-open probe
  retry:
    instances:
      accountProfile:
        maxAttempts: 3                     # 1 call + 2 retries, reads only
        waitDuration: 200ms
  timelimiter:
    instances:
      accountProfile:
        timeoutDuration: 2s
```

## Step 4 — Correlation

`// TODO: log correlationId lab-request-001 when profileFallback fires` (so support can trace a degraded response).

## Predict / Debug

- **Self-invocation of `getProfile()` inside the same class:** annotations do **not** fire — Resilience4j uses AOP proxies, so the call must go through the injected bean/proxy, not `this.getProfile(...)`.
- **`@TimeLimiter` on a sync return:** the method must return a `CompletableFuture` (async), not a plain value; TimeLimiter needs a future to cancel on timeout.
- **Dependencies:** add `spring-boot-starter-aop` (annotations) and Actuator (health/metrics evidence). Retry only idempotent GETs, never blind POST retries.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
