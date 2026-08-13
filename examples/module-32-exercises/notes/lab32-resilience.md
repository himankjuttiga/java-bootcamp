# Lab 32 — Why Resilience

## Step 1 — Scenario

Customer detail for `CUS-1001` Amina calls the Account Profile dependency, which hangs 30s. Three effects:

1. **Thread-pool exhaustion** — each waiting request holds a server thread for 30s; under load the CRM's request threads all block on the hung call and the whole CRM stops serving *any* page, not just the profile.
2. **User-visible stalls / timeouts** — Amina's and Ravi's detail pages spin for 30s and then fail, so a slow dependency becomes a total outage from the user's view.
3. **Cascading failure** — upstream callers (React, load balancer) time out and may retry, piling more load onto the already-hung dependency and the CRM.

## Step 2 — Pattern names

The four Resilience4j ideas: **Retry** (re-attempt transient failures), **Circuit Breaker** (stop calling a failing dependency, fail fast), **Time Limiter** (cap how long a call may take), **Fallback** (return a truthful degraded response instead of hanging/erroring).

## Step 3 — Not a substitute

Resilience wraps calls to survive slowness and transient faults; it does **not** fix a permanently wrong URL, bad credentials, or a logic bug — retrying/opening a circuit around a misconfigured endpoint just fails faster.

## Predict / Debug

- **Account API hangs 30s, no timeout:** CRM threads block until they run out, and the CRM itself becomes unresponsive (the failure spreads).
- **Retry POST create forever:** a non-idempotent POST retried blindly can create duplicate customers/records; aggressive retry is for idempotent reads, not writes.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
