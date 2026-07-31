# Lab 16 — Fill Message Hygiene TODOs

## Step 1 — Copy TODOs
Safe not-found message: "Customer CUS-9999 was not found."
Unsafe message anti-pattern: "ORA-00942: table CUSTOMERS not found; user amina@corp.com at SELECT * FROM ..." (leaks SQL and PII)
Correlation always field: correlationId
Log stack trace? yes (server logs only)
Return stack trace to client? no
@ControllerAdvice live in this pre-lab? no

## Step 2 — Fill blanks
Safe message names the resource without internals; unsafe example exposes SQL and PII; the always-present field is correlationId; stack traces are logged server side (yes) but never returned to the client (no); no live advice wiring in pre-lab (no).

## Step 3 — Correlation always
*Every error sketch includes lab-request-001 (or the incoming request header value).*

## Step 4 — Self-check
Client stack-trace blank is **no**.

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.