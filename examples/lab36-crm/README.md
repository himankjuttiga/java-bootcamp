# Lab 36 — Frontend Security for the CRM SPA

Module 36 · Checkpoint **E** · copied from the course starter into `examples/lab36-crm`,
then completed.

Lab 35 wired the SPA to Spring. Lab 36 hardens that connection: in-memory tokens, an
origin-scoped bearer header, a login flow that leaks nothing, guards that are honest about being
UX, 401 separated from 403, and an XSS proof that customer text cannot execute.

## Runbook

### 1. Start the Spring CRM API (:8080)

```bash
cd ~/java-bootcamp/examples/lab29-crm
mvn spring-boot:run
```

**No `lab35` profile this time.** Lab 35 ran under that profile because the SPA had no login
screen and `/api/customers/**` had to be open. Lab 36 has a login screen, so the default profile
is correct again and `hasAnyRole("AGENT","ADMIN")` is back in force. Retiring that temporary
permit is part of this lab.

Demo credentials from the Spring project (`CrmUserDetailsService`):

| Username | Password | Role |
| -------- | -------- | ---- |
| `agent1` | `agent1` | AGENT |
| `admin1` | `admin1` | ADMIN |

Course demo accounts only. Never use personal or production credentials, and redact the
`Authorization` header in any screenshot.

### 2. Start the SPA (:5173)

```bash
cd ~/java-bootcamp/examples/lab36-crm/crm-ui
cp .env.example .env
npm install
npm run dev
```

### 3. Tests and build

```bash
npm run test -- --run
npm run build
curl -I http://localhost:8080/api/customers
```

No test touches the network; `fetch` is stubbed with `vi.stubGlobal` in every case.

## What was built

| Deliverable | Where |
| --- | --- |
| Threat model | `crm-ui/docs/security-decisions.md` |
| Auth state with `checking` / `anonymous` / `authenticated` + in-memory token | `crm-ui/src/auth/AuthContext.tsx`, `crm-ui/src/auth/tokenStore.ts` |
| Origin-scoped `Authorization` and correlation id | `crm-ui/src/api/http.ts` |
| Login, ProtectedRoute, 401/403, complete logout | `crm-ui/src/pages/LoginPage.tsx`, `crm-ui/src/auth/ProtectedRoute.tsx`, `crm-ui/src/api/ApiError.ts` |
| Open-redirect defence | `crm-ui/src/auth/returnPath.ts` |
| XSS proof | `crm-ui/src/security/xss.test.tsx`, `crm-ui/src/components/CustomerCard.tsx` |
| CSRF N/A rationale | `crm-ui/docs/security-decisions.md` |
| Security headers | `examples/lab29-crm/.../config/SecurityConfig.java`, `crm-ui/vite.config.ts` |
| Abuse tests | `crm-ui/src/security/security.test.tsx` |

Design decisions worth remembering:

* **Memory beats storage because of persistence, not readability.** XSS reads whatever the page
  can reach either way; only Web Storage keeps working tomorrow.
* **`checking` is a real state.** Without it the app renders protected content for one frame
  before the session resolves, which is a PII leak measured in milliseconds.
* **The bearer token is origin-scoped.** One shared helper plus one third-party fetch equals a
  leaked credential, unless the origin is checked.
* **A 401 from the login endpoint is not an expiry.** `http.ts` tells them apart by whether a
  token was attached, so a wrong password does not masquerade as a lost session.
* **Logout unmounts the guarded subtree**, so the customer cache dies with it and Back cannot
  show stale PII.
* **Guards are UX.** The "Check admin access" button is deliberately visible to everyone; Spring
  returns 403 to an AGENT, and that server answer is the actual control.

## Checkpoints

### Checkpoint A — Tooling + model

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | `lab36-crm` in place, Lab 35 http boundary carried forward | Pass |
| 2 | Threat model written; guards ≠ authorization stated | Pass — `docs/security-decisions.md`, Non-controls section |
| 3 | AuthState includes `checking` | Pass |

### Checkpoint B — Session mechanics

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | In-memory `tokenStore` only | Pass — asserted against both Web Storage areas |
| 2 | Bearer attached only to CRM API origin | Pass — third-party fetch asserted header-free |
| 3 | Login generic errors; ProtectedRoute UX | Pass |
| 4 | 401 clears session; 403 does not; logout complete | Pass |

### Checkpoint C — Hardening proofs

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | XSS RTL proof green | Pass — 3 cases, no `img`, `script` or `b` node created |
| 2 | CSRF evidence or documented N/A | Pass — N/A with the bearer rationale and cookie-mode requirements |
| 3 | CSP / security headers evidence | Config Pass on both hosts; `curl -I` capture pending |
| 4 | Abuse tests + build green twice | Pass — 15 tests green on two consecutive runs, build clean |

### Checkpoint D — Hygiene

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | No tokens or passwords in Git or screenshots | Pass — `.env` gitignored, `lab.agent1.AGENT.…` fixture only in tests, redact headers in captures |
| 2 | Security decisions doc complete | Pass |
| 3 | `lab-request-001` on authenticated CRM calls | Pass — asserted in `security.test.tsx` |

## Failure experiments

| # | Experiment | Observed | Restore |
| - | ---------- | -------- | ------- |
| 1 | Token in `localStorage` | Rejected by design; test asserts both storage areas stay empty | Memory only |
| 2 | Third-party URL through the same helper | No `Authorization`, no correlation id | Keep the origin check |
| 3 | Malicious `fullName` rendered | Literal text, no element created | Keep JSX text children |
| 4 | API returns 401 | Token cleared, app drops to login | Keep the expiry branch |
| 5 | API returns 403 | Still signed in, message shown in place | Keep the branches split |
| 6 | Wrong password | One generic message, server wording discarded | Keep generic copy |
| 7 | `returnTo=https://evil.example` | Falls back to `/` | Keep the allowlist |

## Security and production review

* **Untrusted inputs:** all browser input, URL and query values, and every API response field.
* **Enforcement:** Spring Security authorizes each `/api` call. Route guards are UX only.
* **Sensitive values:** the access token lives in memory and is never logged or persisted;
  credentials are course demo accounts; only the public API base URL appears in `VITE_*`.

## Reflection

1. **Which design decision most affected correctness?** Distinguishing 401 from 403 in one place
   inside `http.ts`, and deciding an expiry requires that a token was actually attached. That one
   condition keeps a wrong password from looking like a lost session and keeps a 403 from logging
   people out.
2. **What evidence proves it works?** 15 abuse-case tests green on two consecutive runs covering
   Web Storage, origin scoping, 401, 403, failed login, open redirect, logout and XSS, plus a
   green `tsc -b && vite build`. Live screenshots of the Application tab and header capture remain.
3. **Which failure was hardest to diagnose?** The login 401. A single shared 401 handler is the
   obvious implementation and looks fine until a mistyped password clears a session that never
   existed and the UI reports "your session expired" to someone who was never signed in.
