# Lab 32 starter — timed path (~45 minutes)

**Theme:** Resilience4j — Retry + CircuitBreaker + TimeLimiter + truthful fallback

## Activity card

| | |
| --- | --- |
| **Checkpoint** | **E** |
| **Must prove** | Healthy read · truthful fallback · OPEN fail-fast · timeout · `mvn test` ×2 |
| **Hard gate** | Pre-lab Pass · fallback forbids fake write success |

## 45-minute checklist

- [ ] Add annotations on `AccountProfileService.find` (CircuitBreaker + Retry + TimeLimiter, name `accountProfile`)
- [ ] Fill the truthful `fallback` -> `AccountSummary.unavailable(customerId)`
- [ ] Implement `AccountClient.fetch` (GET `/accounts/{id}/summary`, 5xx -> TemporaryAccountException)
- [ ] Add `AccountProfileResilienceTest` (WireMock: healthy / 503 / slow / OPEN)
- [ ] Run `mvn -B test` twice; capture Actuator + test evidence

## Smoke test

```bash
mvn -B test
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-32/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Retry + CircuitBreaker + TimeLimiter on `find` | Pass / Fail |
| `AccountSummary.unavailable` truthful fallback | Pass / Fail |
| WireMock 503/slow/OK deterministic tests | Pass / Fail |
| Circuit OPEN fail-fast proven | Pass / Fail |
| Tests green twice | Pass / Fail |
