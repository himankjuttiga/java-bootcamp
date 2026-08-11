# Lab 29 — Error Contract (Northstar CRM)

Customer APIs require **Lab 28 Bearer auth** — obtain a token from `POST /api/auth/login` (agent1/agent1) and send `Authorization: Bearer <token>`. Correlation header `X-Correlation-Id: lab-request-001` is echoed into `ErrorResponse.correlationId`.

## ErrorResponse envelope (shipped, unchanged)

`timestamp, status, error, message, correlationId, violations[{field, message}]`. No `path` field, no `rejectedValue`. Passwords and JWTs are never echoed into error bodies.

## Exception to status map

| Exception | Status | Notes |
| --------- | ------ | ----- |
| `MethodArgumentNotValidException` | 400 | `violations[]` from field errors (sorted by field); `message = "Validation failed"` |
| `IllegalArgumentException` | 404 | e.g. `GET /api/customers/CUS-9999` |
| `IllegalStateException` | 409 | duplicate create, e.g. `CUS-1001` |
| `ResponseStatusException` | its own status | e.g. login bad credentials -> 401 (not masked as 500) |
| `Exception` (fallback) | 500 | generic `"Unexpected error"`; no stack trace / SQL to client, logged server-side |

## Evidence commands (with Bearer)

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"agent1","password":"agent1"}' | jq -r .accessToken)

# 400 validation envelope
curl -s -i -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" \
  -H "X-Correlation-Id: lab-request-001" \
  -d '{"id":"","name":"","email":"not-an-email","status":"ACTIVE"}'

# 404 not-found envelope
curl -s -i http://localhost:8080/api/customers/CUS-9999 \
  -H "Authorization: Bearer $TOKEN" -H "X-Correlation-Id: lab-request-001"

# 409 duplicate envelope
curl -s -i -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" -H "Authorization: Bearer $TOKEN" \
  -H "X-Correlation-Id: lab-request-001" \
  -d '{"id":"CUS-1001","name":"Amina Khan","email":"amina.khan@example.com","status":"ACTIVE"}'

# 200 happy GET
curl -s -i http://localhost:8080/api/customers/CUS-1001 \
  -H "Authorization: Bearer $TOKEN" -H "X-Correlation-Id: lab-request-001"

# 401 no token
curl -s -i http://localhost:8080/api/customers/CUS-1001
```

## Tests — ErrorEnvelopeTest (Tests run: 4)

`validationReturns400Envelope`, `missingCustomerReturns404Envelope`, `duplicateReturns409Envelope`, `securityStillRequiresToken` (no token -> 401).

## Lab 14 / Lab 16 unification

The DTO constraint ideas from Lab 14 and the centralized exception-handler ideas from Lab 16 are now one Spring Boot contract: Bean Validation on `CustomerRequest` (triggered by `@Valid`) plus a single `@RestControllerAdvice` (`GlobalExceptionHandler`) that maps every failure to the shared `ErrorResponse` envelope. One handler, one shape, every client renders one error component.

## Failure experiments

| # | Experiment | Observed | Restore |
| - | ---------- | -------- | ------- |
| 1 | Remove `@Valid` temporarily | bad email reaches service (no 400) | restore `@Valid` |
| 2 | Blank name / bad email | 400 + violations | keep constraints |
| 3 | `CUS-9999` / duplicate `CUS-1001` | 404 / 409 envelopes | keep mappings |
| 4 | Call customers without Bearer | 401 | keep security |
| 5 | Force unhandled exception | safe 500 body; details in logs only | keep fallback |
