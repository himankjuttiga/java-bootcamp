# Lab 12 — Target API Sketch

## Step 1 — Methods

The target `CustomerService` API should expose:

- `findById(String customerId)` — returns the Customer for a given ID (e.g. `CUS-1001`), or a not-found result when absent.
- `activateProspect(String customerId)` — transitions a PROSPECT customer to ACTIVE.
- `validateStatus(String customerId)` — confirms the current status is valid before any transition (optional support method).

## Step 2 — Ravi path

`activateProspect(CUS-1002)` moves Ravi from PROSPECT to ACTIVE.

## Step 3 — Keep out

Explicitly exclude the following from this sketch:

- SOAP endpoints
- Spring controllers

This sketch is the clean service API only, not the transport or web layer.

## Step 4 — Prep boundary

*Do not complete full Lab 12 refactor in pre-lab.*

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.

## Fixtures (self-check)

| Customer | ID | Status |
| --- | --- | --- |
| Amina | CUS-1001 | ACTIVE |
| Ravi | CUS-1002 | PROSPECT |

Correlation ID: `lab-request-001`