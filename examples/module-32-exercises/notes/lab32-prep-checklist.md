# Lab 32 prep checklist

## Earlier exercise files present?

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/lab32-resilience.md | yes |
| notes/lab32-circuit-states.md | yes |
| notes/lab32-fallback-contract.md | yes |
| notes/lab32-pattern-map.md | yes (Ex 5 card not provided — content inferred) |
| notes/lab32-todos.md | yes |

## Fixtures (verify)

| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE |
| CUS-1002 | Ravi Singh | PROSPECT |

Instance: `accountProfile`. Correlation: lab-request-001.

## Scope statement

Pre-lab only — prepare for lab; do not complete full Lab 32 now.

## Teach-back

Lab 32 wraps the CRM's outbound Account Profile call with Resilience4j: TimeLimiter bounds a slow call, Retry re-attempts transient failures on idempotent reads, CircuitBreaker trips to OPEN after repeated failures so the dependency (WireMock) is not hammered, and a Fallback returns a truthful degraded `AccountSummary.unavailable` (available=false) rather than a fake success. Evidence comes from WireMock stubs (503 / slow / OK for CUS-1001), Actuator health/metrics showing the circuit state, and tests asserting fallback fires and the circuit opens. This is HTTP outbound resilience, not Kafka (Lab 30/31) and not React UI (Lab 33).

## Evidence preview

WireMock request-count assertions (OPEN stops calls), circuit-state transition, fallback response for CUS-1001, Actuator `resilience4j`/health metrics.

## Self mark

Overall prep: Pass

If Fail, revisit exercise(s): the one owning the missing artifact (states -> Ex 4, fallback -> Ex 2, TODOs -> Ex 3).

## Predict / Debug

- **Prove OPEN without WireMock request count?** Harder but yes — you can assert the circuit-breaker state transitions to OPEN via Actuator/metrics or the CircuitBreakerRegistry; the request-count assertion is the cleanest proof that OPEN stops outbound calls, so keep it.
- **Start React Module 33 early?** Park the UI — error toasts are Lab 33; Lab 32 is backend resilience only.
