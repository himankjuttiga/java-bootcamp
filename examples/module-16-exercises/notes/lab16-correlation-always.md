# Lab 16 — Correlation on Every Error

## Step 1 — Success path
activateCustomer(Ravi, CUS-1002) success still echoes and logs correlationId lab-request-001. The 200 response body and the log line both carry the same id.

## Step 2 — Failure path
notFound(CUS-9999) failure response includes the same correlation field: correlationId lab-request-001. Success and error paths share one correlation contract.

## Step 3 — Missing header
Policy idea: when the incoming correlation header is missing, generate one (for example a UUID), attach it to the response and logs, and propagate it downstream. Note for later labs; never trust an inbound id for identity or authorization.

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.