# Lab 29 — Exception to Status Map

| Case | Status | Code |
| --- | --- | --- |
| Invalid body (blank name / bad email) | 400 | VALIDATION_FAILED |
| CUS-9999 (unknown id) | 404 | CUSTOMER_NOT_FOUND |
| Duplicate CUS-1001 | 409 | DUPLICATE_CUSTOMER |
| Illegal status transition | 400 (or 422) | ILLEGAL_TRANSITION |
| Unexpected server error | 500 | INTERNAL_ERROR (generic, no stack trace) |

## Answers to the prompts

- **Is 500 acceptable for an expected not-found?** No — a missing customer is an expected, client-correctable condition and must be 404 `CUSTOMER_NOT_FOUND`. Reserve 500 for genuinely unexpected failures.
- **Duplicate: 400 or 409 in this lab?** 409 Conflict (`DUPLICATE_CUSTOMER`) — the request is well-formed but conflicts with existing state, which is a conflict, not a validation error.

Fixtures: Amina `CUS-1001`/ACTIVE, Ravi `CUS-1002`/PROSPECT, not-found `CUS-9999`, correlation `lab-request-001`.

## Scope

Pre-lab only.
