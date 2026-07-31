# Lab 16 — Northstar CRM API Exception Handling

Extends `lab15-crm` with a consistent API error model: `BusinessException`,
`ErrorResponse`, and a `GlobalExceptionHandler` that maps validation, not-found,
and business-rule failures to one JSON shape, always carrying a correlation id.
No HTTP server; the `CustomerApiFacade` returns `ApiResult` (Ok / Fail).

## Fixtures
| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE |
| CUS-1002 | Ravi Singh | PROSPECT |
| CUS-9999 | — | not-found demo |

Correlation id on every failure: `lab-request-001`.

## Error contract (one shape, every failure)
`timestamp, status, error (stable code), message, correlationId, errors{}` —
`errors` is always present (empty `{}` when there are no field errors).

## Status-code choices (lab standard)
| Case | status | error code |
| ---- | -----: | ---------- |
| Bean Validation failure | 400 | `VALIDATION_FAILED` |
| Customer not found | 404 | `CUSTOMER_NOT_FOUND` |
| Illegal transition / duplicate | 409 | `BUSINESS_CONFLICT` |
| Unexpected defect | 500 | `INTERNAL_ERROR` |

**409 vs 422:** an illegal transition (e.g. ACTIVE -> PROSPECT) is a state
conflict on a well-formed request, so 409 fits; 422 is reserved for
semantically unprocessable input. Applied consistently across validator,
service, and handler. Full rationale: [`docs/error-model-notes.md`](docs/error-model-notes.md).

## Catch order
Specific before generic. In `CustomerApiFacade`: `BusinessException` first, then
duplicate-policy `IllegalStateException`, then a generic `Exception` fallback —
so 404/409 never collapse into 500.

## Message hygiene
`message` never contains stack traces, SQL, or PII. The 500 path returns a fixed
"Unexpected server error"; the original exception is logged server-side only.

## Run it
```bash
mvn -q clean test          # suite: 19 tests, BUILD SUCCESS
mvn -q exec:java -Dexec.mainClass=com.northstar.crm.Main   # prints 400/404/409 JSON
```

## Evidence
Screenshots under [`notes/screenshots/lab-16/`](notes/screenshots/lab-16/):
test suite green, `Main` demo JSON (400/404/409 with `lab-request-001`,
CUS-1001 stays ACTIVE), and `GlobalExceptionHandlerTest` green.

## Forward map (later module)
`GlobalExceptionHandler` previews Spring `@RestControllerAdvice`; the three
`from*` methods become `@ExceptionHandler` methods returning the same
`ErrorResponse`, with the correlation id promoted to an SLF4J MDC value.
