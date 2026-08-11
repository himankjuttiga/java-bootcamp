# Lab 28 starter — timed path (~45 minutes)

**Theme:** SecurityFilterChain + JWT — AGENT/ADMIN CRM API protection

## Activity card

| | |
| --- | --- |
| **Objective** | Complete JWT/filter TODOs and prove 401 vs 403 vs 200 |
| **Skills practiced** | SecurityFilterChain, JwtService, role matchers, security notes |
| **Expected outcome** | Login token · Bearer CUS-1001 · agent admin 403 · no secrets in Git |
| **Estimated time** | ~45 minutes |
| **Files** | `examples/lab28-crm/` copied from this starter |

## 45-minute checklist

- [ ] Complete `SecurityFilterChain` matchers (login + health + `/error` permitAll; AGENT customers; ADMIN admin)
- [ ] Fill `JwtService` lab stub (`lab.subject.role.sig`) — real `eyJ` HS256 is full-path optional
- [ ] Wire `JwtAuthenticationFilter` into the chain; login returns `{accessToken, tokenType}`
- [ ] Prove login → Bearer GET CUS-1001; missing token → 401; agent on admin → 403
- [ ] Add `SecurityPathTest` (**Tests run: 3**)
- [ ] Note IdP/key-rotation in docs/security-notes.md (`JWT_SECRET`)

## Smoke test

```bash
mvn -B test
# Tests run: 3
mvn -B spring-boot:run
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-28/` (redact secrets).

## Run

```bash
export JWT_SECRET='lab-only-change-me'   # never commit a real value
mvn -q spring-boot:run
# login as agent1/agent1 or admin1/admin1 -> capture accessToken (redact)
# GET /api/customers/CUS-1001 with Authorization: Bearer <token>
```

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| SecurityFilterChain bean present and stateless | Pass / Fail |
| Login endpoint issues a token stub | Pass / Fail |
| 401 vs 403 distinguished in notes or tests | Pass / Fail |
| Bearer GET CUS-1001 works for AGENT | Pass / Fail |
| No real secrets committed | Pass / Fail |
