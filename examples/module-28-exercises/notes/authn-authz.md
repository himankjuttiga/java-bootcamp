# Lab 28 — Authentication Versus Authorization

| Concept | Question | CRM example | HTTP |
| --- | --- | --- | --- |
| Authentication | Who are you? | Call `/api/customers/CUS-1001` with a missing, malformed, or expired JWT | 401 |
| Authorization | What may you do? | `agent1` (AGENT) calls an ADMIN-only route `/api/admin/**` with a valid token | 403 |

## Correlation vs auth

The `X-Correlation-Id: lab-request-001` header is only a tracing/evidence id. It identifies a request for logging, not a user, and grants no access. Authentication comes from the `Authorization: Bearer <JWT>` credential, never from the correlation id.

## Answers to the prompts

- **Expired JWT on a permitted role -> 401.** An expired token fails authentication (identity no longer valid), so the caller is treated as unauthenticated regardless of role.
- **Valid AGENT token on `/api/admin` -> 403.** The caller is authenticated but lacks the ADMIN role, so it is a permission failure.

Lab users: `agent1` (AGENT), `admin1` (ADMIN). Fixtures: Amina `CUS-1001`/ACTIVE, Ravi `CUS-1002`/PROSPECT. Never write real JWT secrets.

## Scope

Pre-lab only.
