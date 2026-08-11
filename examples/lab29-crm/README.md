# Lab 29 starter — timed path (~45 minutes)

**Theme:** Bean Validation + GlobalExceptionHandler + stable ErrorResponse on the secured CRM

## Activity card

| | |
| --- | --- |
| **Objective** | Fill validation + error-envelope TODOs (Lab 28 security is already provided) |
| **Skills practiced** | `@Valid` DTOs, `@RestControllerAdvice`, 400/404/409 envelopes, MockMvc body asserts |
| **Expected outcome** | invalid -> 400 · CUS-9999 -> 404 · duplicate -> 409 · happy GET · tests green |
| **Estimated time** | ~45 minutes |
| **Files** | `examples/lab29-crm/` copied from this starter |

## 45-minute checklist

- [ ] Annotate `CustomerRequest` (`@NotBlank` id/name/email/status, `@Email` email)
- [ ] Add `@Valid` on `CustomerController.create`
- [ ] Fill `GlobalExceptionHandler` (400 validation, 404 IllegalArgument, 409 IllegalState, safe 500)
- [ ] Add `ErrorEnvelopeTest` (**Tests run: 4**, includes no-token 401)
- [ ] Fill `docs/error-contract.md`

## Run (customer APIs require a Bearer token)

```bash
export JWT_SECRET='lab-only-change-me'
mvn -q spring-boot:run
# login agent1/agent1 -> capture accessToken -> call /api/customers with Authorization: Bearer <token>
mvn -B test   # Tests run: 4
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-29/` (redact tokens).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Bean Validation on `CustomerRequest` + `@Valid` | Pass / Fail |
| GlobalExceptionHandler returns `ErrorResponse` for 400/404/409 | Pass / Fail |
| Safe 500 (no stack trace) | Pass / Fail |
| `ErrorEnvelopeTest` Tests run: 4 | Pass / Fail |
| No secrets / `target/` committed | Pass / Fail |
