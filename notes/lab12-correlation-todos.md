# Lab 12 — Fill Correlation One-Liner TODOs

## Step 1 — Copy TODOs

Correlation id value: `lab-request-001`
Log on activate entry: `activateProspect entry customerId=CUS-1002 correlationId=lab-request-001`
Log on activate success for Ravi: `activateProspect success customerId=CUS-1002 PROSPECT->ACTIVE correlationId=lab-request-001`
Never log field: raw email (PII)
Place correlation in: MDC

## Step 2 — Fill blanks

Fill with `lab-request-001`, short log phrases, and a PII field you must not log (e.g. raw email if present later).

## Step 3 — One-liner rule

*Every public service entry logs correlation once.*

## Step 4 — Self-check

Correlation blank is exactly `lab-request-001`. Confirmed.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.

## Fixtures (self-check)

| Customer | ID | Status |
| --- | --- | --- |
| Amina | CUS-1001 | ACTIVE |
| Ravi | CUS-1002 | PROSPECT |

Correlation ID: `lab-request-001`