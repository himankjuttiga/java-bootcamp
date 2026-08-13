# Lab 32 — Resilience notes (Northstar Account Profile)

Instance: `accountProfile`. Correlation: `lab-request-001`.

## UX / API contract

| CRM response field | Meaning |
| ------------------ | ------- |
| `available: true` | Account dependency succeeded; the accounts data is trustworthy. |
| `available: false` | Degraded read; show the "Account information is temporarily unavailable" banner. Do **not** invent balances/tier. |
| `note` | `account-profile-unavailable` on degraded reads. |
| Correlation | `lab-request-001` (or request header) carried into CRM + outbound logs. |

**Honesty rule:** the fallback is a truthful *degraded read* only. Write endpoints (create/update) must **never** use a success-shaped fallback — a failed write fails loudly, it is not reported as OK.

## Pattern composition

- `@TimeLimiter(accountProfile)` — 1.5s budget; the method returns `CompletableFuture` so the slow call is cancelled instead of hanging a servlet thread.
- `@Retry(accountProfile)` — bounded (maxAttempts 3, 200ms) for transient failures (HTTP 5xx mapped to `TemporaryAccountException`). Reads only; never blind write retries.
- `@CircuitBreaker(accountProfile)` — count window 6, min 4 calls, opens at 50% failure. When OPEN, calls fail fast and the fallback runs without touching the Account API.
- `fallback(customerId, Throwable)` — returns `AccountSummary.unavailable(customerId)` (available=false).

Call `find(...)` through the injected Spring bean (no `this.find(...)` self-invocation) so the AOP proxies apply.

## Circuit states

CLOSED (calls flow, failures counted) -> OPEN at threshold (fail fast, dependency not hammered, stays open `waitDurationInOpenState` = 2s) -> HALF_OPEN (a few probe calls) -> CLOSED on success or back to OPEN on failure.

## Runbook

```bash
cd ~/java-bootcamp/examples/lab32-crm
mvn -B test                # WireMock-driven, no external service needed; run twice for determinism
mvn -B spring-boot:run
curl -s localhost:8080/actuator/health
curl -s localhost:8080/actuator/circuitbreakerevents
curl -s localhost:8080/actuator/metrics/resilience4j.circuitbreaker.calls
```

## Tests (AccountProfileResilienceTest — WireMock)

- `healthyDependencyReturnsAvailable` — 200 stub -> available=true.
- `failingDependencyFallsBackUnavailable` — 503 stub -> available=false, honest fallback.
- `slowDependencyTimesOutToFallback` — 3s delay -> fallback within ~1.5s (TimeLimiter).
- `circuitOpensAndFailsFast` — repeated 503 -> circuit OPEN; WireMock receives fewer requests than calls made (fail-fast).

## Production caution

Lab CB window (6 calls) and 2s open-wait are for classroom visibility only. Production thresholds and timeouts come from SLOs and load tests, not these demo values. Use PLAINTEXT/dev settings for the lab; production needs TLS and careful token propagation on outbound headers (never commit tokens to WireMock journals).
