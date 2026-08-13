# Lab 32 — Pattern Map

> Note: Exercise 5's card was not provided; this maps each failure mode to the Resilience4j pattern for the `accountProfile` dependency (CUS-1001 / CUS-1002). Adjust to the exact template if your card differs.

## Failure mode -> pattern

| Failure mode (Account Profile) | Resilience4j pattern | Instance / effect |
| --- | --- | --- |
| Call hangs / is slow (30s) | **TimeLimiter** | `accountProfile`, cap at 2s, cancel the future |
| Transient error / brief 503 / network blip | **Retry** | `accountProfile`, maxAttempts 3, reads only |
| Repeated failures over the window | **CircuitBreaker** | `accountProfile`, open at 50% failure rate, fail fast |
| Circuit OPEN or all attempts exhausted | **Fallback** | `profileFallback` -> `AccountSummary.unavailable` (available=false) |
| Protect CRM threads from one slow dependency | **Bulkhead** (optional) | isolate `accountProfile` calls so they cannot exhaust the whole pool |

## Order of application

TimeLimiter (bound the call) -> Retry (re-attempt transient) -> CircuitBreaker (trip on repeated failure) -> Fallback (honest degraded response). All share the instance name `accountProfile` so they act on the same dependency.

## Key rules

- Retry is for **idempotent reads** (GET profile), never blind POST/write retries.
- Fallback returns a truthful degraded read (`available=false`), never a fake success.
- Annotations require AOP and calling through the proxy (no self-invocation).

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
