# Lab 29 readiness checklist

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/dto-constraints.md | yes |
| notes/lab29-handler-todos.md | yes |
| notes/error-envelope.md | yes |
| notes/exception-status-map.md | yes |
| notes/mockmvc-body-plan.md | yes |

## Scope

Pre-lab only. Stack traces to clients? no — the generic 500 returns a safe envelope (status, code, message, correlationId) with no stack trace, SQL, or secrets. Envelope and status map are clear; validation triggers via `@Valid`.

## Self mark

Overall prep: Pass

If Fail, revisit: whichever exercise owns the missing or incorrect artifact.

## Answers to the prompts

- **If `mockmvc-body-plan.md` is missing:** reopen Exercise 6 and recreate it.
- **Does Lab 29 replace Lab 28's SecurityFilterChain?** No — Lab 29 layers Bean Validation and the `ErrorResponse` handler on top of the secured API; the Lab 28 security baseline stays in place.
