# Lab 28 — MockMvc Evidence Matrix

| Case | Auth | Route | Expect |
| --- | --- | --- | --- |
| Anonymous customers | none (no Authorization header) | GET /api/customers/CUS-1001 | 401 |
| Agent admin | agent1 valid Bearer (AGENT) | GET /api/admin/... | 403 |
| Agent customer | agent1 valid Bearer (AGENT) | GET /api/customers/CUS-1001 | 200 |
| Bad token | garbage/malformed Bearer | GET /api/customers/CUS-1001 | 401 |
| Admin admin (optional) | admin1 valid Bearer (ADMIN) | GET /api/admin/... | 200 |

## Answers to the prompts

- **Login success in the matrix or separate?** Keep login as its own test (POST `/api/auth/login` -> 200 + token). The matrix focuses on protected-route authz; a successful login is a precondition, cleaner as a dedicated case.
- **Bad token vs missing token, tested separately:** they exercise different paths — a missing token means no authentication was attempted, a bad token means authentication was attempted and rejected (bad signature / expired / malformed). Both should return 401, and testing both guards against a filter that only handles one.

Fixtures: Amina `CUS-1001`/ACTIVE, Ravi `CUS-1002`/PROSPECT, correlation `lab-request-001`, users `agent1`/`admin1`.

## Scope

Pre-lab only.
