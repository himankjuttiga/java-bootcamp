# Lab 36 prep checklist

## Earlier exercise files present?

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/lab36-security.md | yes |
| notes/lab36-token-storage.md | yes |
| notes/lab36-xss-csp.md | yes |
| notes/lab36-csrf-notes.md | yes |
| notes/lab36-todos.md | yes |

## Fixtures (verify)

| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE |
| CUS-1002 | Ravi Singh | PROSPECT |

Fake token placeholder: `lab-token-001`. Correlation id: `lab-request-001`. No real secrets appear in
any of these notes.

## Decisions carried into the lab

| # | Decision | Rationale |
| - | -------- | --------- |
| 1 | Access token in memory only, never `localStorage` or `sessionStorage` | bounds one XSS hit to a single page view instead of granting a token that survives indefinitely; refresh-logs-out is the accepted cost |
| 2 | Bearer header attached in one place, scoped to the CRM API origin | no cookie is auto-attached, so classic CSRF is out of scope, and the credential cannot leak to a third-party host through the shared fetch helper |
| 3 | Route guards are UX, Spring Security is authorization | a bypassed `ProtectedRoute` yields 401 from the API, not leaked data; Lab 36 restores `hasAnyRole("AGENT","ADMIN")` over the temporary Lab 35 `permitAll` |

## Deferred

A full OIDC or external identity provider is out of scope. This lab uses the existing Spring
`/api/auth/login` endpoint issuing a JWT, with the token model and CSRF stance written into
`docs/security-decisions.md`. Refresh-token rotation and CSP enforcement headers are documented, not
implemented.

## Entry gates

| Gate | State |
| ---- | ----- |
| Lab 35 http boundary available | yes, `examples/lab35-crm/crm-ui/src/api/http.ts` is a single fetch boundary with a header slot ready for `Authorization` |
| Spring CRM API reachable | yes, `examples/lab29-crm` runs on `:8080`; the `lab35` profile is the one to retire in this lab |
| Login endpoint present | yes, `/api/auth/login` with `AuthController` and `JwtService` from Lab 28 |

## Scope statement

Pre-lab only — prepare for lab; do not complete full Lab 36 now.

## Self mark

Overall prep: Pass
If Fail, revisit exercise(s): n/a
