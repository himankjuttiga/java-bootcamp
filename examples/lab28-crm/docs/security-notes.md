# Lab 28 — Security Notes (Northstar CRM)

Correlation header: `X-Correlation-Id: lab-request-001` (tracing only, not a credential).

## Auth runbook

```bash
export JWT_SECRET='lab-only-change-me'   # never commit a real value
mvn -q spring-boot:run
```

1. Login (open endpoint):

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"agent1","password":"agent1"}'
# {"accessToken":"lab.agent1.AGENT.<sig>","tokenType":"Bearer"}
```

2. Call a protected route with the token (redact the token in screenshots):

```bash
curl -s http://localhost:8080/api/customers/CUS-1001 \
  -H "Authorization: Bearer <token>" -H "X-Correlation-Id: lab-request-001"
```

## Demo users

| username | password | role |
| -------- | -------- | ---- |
| agent1 | agent1 | AGENT |
| admin1 | admin1 | ADMIN |

## Matcher table

| Path | Rule |
| ---- | ---- |
| `/api/auth/login`, `/actuator/health`, `/error` | permitAll |
| `OPTIONS /**` | permitAll (CORS preflight) |
| `/api/admin/**` | hasRole("ADMIN") |
| `/api/customers/**` | hasAnyRole("AGENT","ADMIN") |
| anything else | authenticated (default deny) |

Session is STATELESS; CSRF disabled (Bearer API, no session cookie). Unauthenticated requests return **401** via the authentication entry point; authenticated-but-wrong-role returns **403**. `/error` is permitAll so live Tomcat does not rewrite a 403 into a 401.

## 401 vs 403

- Missing / malformed / tampered token -> **401** (not authenticated).
- Valid AGENT token on `/api/admin/**` -> **403** (authenticated, wrong role).

## Evidence (SecurityPathTest — Tests run: 3)

- `missingTokenIs401` — no token on customers -> 401.
- `agentCanReadCustomerButNotAdmin` — AGENT: customers 200, admin 403.
- `adminCanPing` — ADMIN: admin 200.

## Production IdP / secret-rotation checklist

- Prefer an enterprise IdP / OAuth2 (Keycloak, Okta, Entra ID) in production; the lab stub token is teaching mode only.
- Store the signing secret in a secret manager; rotate on schedule and on incident. Support key ids for overlap during rotation.
- HTTPS only; short access-token TTL; longer, revocable refresh tokens.
- Never log raw Bearer tokens, secrets, or passwords; audit failed logins and access denials.
- Least privilege; review ADMIN grants regularly.
- If `JWT_SECRET` is ever committed: rotate immediately, invalidate old tokens, purge from history, move to a secret manager.
