# Lab 25 — AI Review Policy

## Must reject

- `ResponseEntity` or any HTTP/web type inside `CustomerService`.
- Controller importing or calling `CustomerRepository` directly (controller → repository shortcut).
- Field `@Autowired` instead of constructor injection.
- Invented JPA/database wiring mid-lab (scope is in-memory this lab).
- Any secrets or PII (names, emails) in logs.

## Must check

- Fixtures preserved: CUS-1001 (Amina, ACTIVE) / CUS-1002 (Ravi, PROSPECT).
- Constructor DI with `final` fields throughout.
- Duplicate-check and not-found rules live in the service.
- Tests still meaningful (not deleted or weakened to pass).

## Where to record review

`docs/lab25-001.md` — record each AI suggestion as accept/reject with a one-line reason (or "N/A — AI not used" if you wrote it by hand).

## Debug / design challenge

Reject. Copilot suggesting `@Autowired` fields on `CustomerService` violates the course policy — convert to constructor injection with `final` fields (required, testable, immutable).

## Predict the output / behavior

If you did not use AI, write "N/A — AI not used; code authored by hand" in `docs/lab25-001.md`. The record still exists, just noting no AI drafts were accepted.

## Scope

Pre-lab only.
