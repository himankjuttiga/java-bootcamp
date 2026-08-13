# Lab 32 — Circuit States

Instance: `accountProfile` (outbound HTTP to Account Profile).

## Step 1 — Closed

Normal state: calls flow through to the Account API, and failures are counted. If the failure rate over the sliding window exceeds the threshold, the breaker trips to OPEN.

## Step 2 — Open

Calls **fail fast** without touching the Account API (the fallback runs immediately), so the failing/slow dependency (WireMock in tests) is not hammered while it recovers. Stays OPEN for `waitDurationInOpenState`.

## Step 3 — Half-open

After the wait, a limited number of **trial calls** are allowed through to probe recovery: if they succeed the breaker returns to CLOSED; if they fail it snaps back to OPEN.

## Step 4 — Diagram

```
        failure rate exceeded
 CLOSED ----------------------> OPEN
   ^                              |
   | trial calls succeed          | wait duration elapses
   |                              v
   +--------- HALF_OPEN <---------+
                  |
                  | trial call fails
                  v
                OPEN
```

## Predict / Debug

- **In OPEN, do calls hit the Account API?** No — OPEN short-circuits to the fallback; that is the whole point (protect the dependency and fail fast).
- **CB never opens in tests:** usually the wrong instance name (annotation `name` must match the YAML `resilience4j.circuitbreaker.instances.accountProfile`), or too few calls / too high a threshold to trip the window.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
