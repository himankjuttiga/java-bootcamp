# Lab 16 — Error Model Notes (Northstar CRM)

## One JSON shape
Every failure path returns the same `ErrorResponse`:
`timestamp, status, error (stable code), message, correlationId, errors{}`.
`errors` is always present (empty `{}` when there are no field errors).

## Status mapping (lab standard)
| Case | status | error code | Where it is raised |
| ---- | -----: | ---------- | ------------------ |
| Bean Validation failure | 400 | `VALIDATION_FAILED` | `GlobalExceptionHandler.fromValidation` |
| Customer not found | 404 | `CUSTOMER_NOT_FOUND` | `BusinessException.notFound` |
| Illegal transition / duplicate | 409 | `BUSINESS_CONFLICT` | `BusinessException.conflict` |
| Unexpected defect | 500 | `INTERNAL_ERROR` | `GlobalExceptionHandler.fromUnexpected` |

## Why 409 (not 422) for illegal transitions
The request is well formed and understood; it fails because it clashes with the
current resource state (e.g. ACTIVE -> PROSPECT). That is a state conflict, which
409 signals precisely. 422 is reserved for input that is syntactically valid but
semantically unprocessable, which is not the case here. The choice is applied
consistently across the validator, service, and handler.

## Catch order
Specific before generic. In `CustomerApiFacade`: `BusinessException` first, then the
duplicate-policy `IllegalStateException`, then a generic `Exception` fallback. A broad
catch first would shadow the domain mapping and turn 404/409 into 500.

## Message hygiene
`message` never contains stack traces, SQL, or PII. The 500 path returns a fixed
"Unexpected server error" string; the original exception (stack + message) is logged
server-side only. Correlation id `lab-request-001` rides on every response and log.

## Forward map to Spring (later module)
`GlobalExceptionHandler` previews `@RestControllerAdvice`; `fromBusiness` /
`fromValidation` / `fromUnexpected` become `@ExceptionHandler` methods returning the
same `ErrorResponse` contract, with the correlation id promoted to an SLF4J MDC value.
