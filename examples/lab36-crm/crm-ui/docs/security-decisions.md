# Lab 36 — Security decisions

## Threat model

### Assets

| Asset | Why an attacker wants it |
| --- | --- |
| Access token | Replays as the signed-in agent against every `/api/customers` call |
| Customer PII (Amina `CUS-1001`, Ravi `CUS-1002`) | Names, emails and status are regulated data |
| Admin actions (`/api/admin/**`) | An ADMIN token escalates instantly |
| Correlation and log data | Exposes internal ids and request flow |

### Untrusted inputs

Everything the browser touches: form fields, URL path and query, `returnTo` destinations, and
every field of every API response. A customer name is untrusted even though it came from our own
API, because any writer upstream can poison it.

### Trust boundaries

The browser is untrusted. Spring Security authorizes every `/api` request. The SPA holds no
authority whatsoever, only convenience.

### Threats and controls

| Threat | Control | Where |
| --- | --- | --- |
| XSS stealing the token | JSX text children only, no HTML sinks | `components/CustomerCard.tsx`, proven in `security/xss.test.tsx` |
| Token persistence after XSS | In-memory `tokenStore`, no Web Storage | `auth/tokenStore.ts`, proven in `security/security.test.tsx` |
| Token exfiltration to third parties | Bearer attached only to the CRM API origin | `api/http.ts` |
| CSRF | N/A for bearer, see below | `api/http.ts` |
| Open redirect after login | Internal-path allowlist for `returnTo` | `auth/returnPath.ts` |
| Privilege confusion | 401 and 403 handled separately | `api/http.ts`, `api/ApiError.ts` |
| Session survives logout | Token cleared and guarded subtree unmounted | `auth/AuthContext.tsx` |
| Secrets in the bundle | Only the public API base URL in `VITE_*` | `.env.example` |

### Non-controls

`ProtectedRoute` is **not** authorization. Neither is hiding a button by role. Both are user
experience. Anyone can bypass them with a URL, devtools, or curl, and the only thing that stops
them is Spring Security returning 401 or 403. The "Check admin access" button is rendered for
every user on purpose, to make that point visible: an AGENT can click it and gets 403 from the
server, not a hidden button.

## Token storage

**In-memory only**, in a module variable inside `auth/tokenStore.ts`.

The reason is persistence, not readability. Script running in our origin can read anything the
page can reach, so `localStorage` is not "less secure to read" in the moment. It is worse because
the token is still there tomorrow: one XSS hit yields a credential that keeps working long after
the payload is gone. A module variable dies with the tab.

Accepted cost: refreshing signs the user out, because there is nothing to rehydrate from. In
production the fix is a short-lived access token in memory refreshed by an `HttpOnly` `Secure`
`SameSite` cookie, not a longer-lived token parked in Web Storage. A refresh token in
`localStorage` "for convenience" trades a one page-view compromise for a permanent one.

Never committed, never logged. Logs carry the correlation id `lab-request-001` instead, which is
traceable without being a credential.

## Origin-scoped Authorization

`api/http.ts` resolves every request to an absolute `URL` and compares `url.origin` to
`apiOrigin`, derived from `VITE_API_BASE_URL`. `Authorization` and `X-Correlation-Id` are set only
when those match. A fetch to `https://evil.example/collect` through the same helper carries
neither header, which is asserted in `security.test.tsx`.

Without this check, one component fetching an avatar, a CDN asset or an analytics endpoint through
the shared helper would hand our bearer token to that host.

## 401 vs 403

| Code | Meaning | Behaviour |
| --- | --- | --- |
| 401 | No valid credential, or it expired | `tokenStore.clear()`, notify subscribers, `AuthContext` drops to `anonymous`, login screen returns |
| 403 | Valid credential, insufficient role | `ForbiddenError` thrown, session untouched, message shown in place |

Treating 403 as 401 logs users out for merely lacking a role, sending them to re-authenticate
against a wall that will never open. Both are proven in `security.test.tsx`, including the case
that matters most: a 401 from `/api/auth/login` is a bad password, not an expired session, so it
must not clear anything or fire the expiry listeners. `http.ts` distinguishes them by whether a
token was attached to the request in the first place.

## CSRF — N/A for this design, with rationale

CSRF exists because the browser attaches **cookies** automatically based on the destination,
without the sending page knowing anything. This SPA holds its token in JavaScript memory and
attaches it explicitly as `Authorization: Bearer …`. A cross-site page cannot read that token,
and the browser will not attach it on their behalf, so a forged cross-site request arrives at
Spring with no credential and is rejected as anonymous. That is why `csrf.disable()` on a
stateless bearer API is defensible rather than negligent.

This is not "CSRF does not exist". The risk moved: bearer tokens shift exposure to XSS, since any
script in our origin can read and use the token directly. Cookies with `HttpOnly` protect against
reading but reintroduce CSRF. There is no option with neither risk, only a choice about which one
you are equipped to control.

If this app ever switches to cookie sessions, the following become mandatory:

* `SameSite=Lax` at minimum, `Strict` where the flows allow it, plus `HttpOnly` and `Secure`
* A CSRF token on every unsafe method: `credentials: 'include'` with an `X-XSRF-TOKEN` header
* Proof that a missing CSRF token yields 403 and a valid one yields 201

## CSP and security headers

Both hosts set headers, because the SPA and the API are different origins.

API (`examples/lab29-crm`, `config/SecurityConfig.java`, applied to both profile chains):

```text
Content-Security-Policy: default-src 'none'; frame-ancestors 'none'; object-src 'none'
Referrer-Policy: no-referrer
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
```

The API serves JSON only, so `default-src 'none'` costs nothing and is the strictest honest
policy available.

SPA dev host (`vite.config.ts`, `server.headers`):

```text
Content-Security-Policy: default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; ...
```

The dev policy allows inline and eval because Vite's HMR client requires them. Production serves a
built bundle and tightens `script-src` to `'self'` with a nonce or hashes. That gap is stated here
rather than hidden, because a CSP that quietly needs `'unsafe-inline'` forever is theatre.

HTTPS and HSTS are production concerns: `Strict-Transport-Security: max-age=31536000;
includeSubDomains` once the app is served over TLS. The lab runs on plain HTTP on localhost.

CSP is defence in depth. Correct escaping is the control; CSP is the net underneath it.

## Login hardening

* One generic failure message, `Invalid username or password`, for bad password and unknown user
  alike. The server's wording is deliberately discarded so the UI cannot confirm which accounts
  exist.
* `autoComplete="username"` and `autoComplete="current-password"` so password managers behave.
* Submit disabled while in flight, guarded by a ref so the first click blocks the second
  synchronously.
* `returnTo` passed through `safeReturnPath`, which rejects absolute URLs, protocol-relative
  `//evil.example`, and `javascript:` before any navigation happens.

## Deviation from the GUIDE, stated openly

The GUIDE's `ProtectedRoute` uses `<Navigate to="/login" replace state={{ from: location }} />`.
The starter's `package.json` has no `react-router-dom` dependency, and the instruction was to match
the starter, so the anonymous branch renders `<LoginPage />` in place instead. The security
properties are identical: protected content never renders for an anonymous user, the attempted path
is sanitised through the same allowlist, and the API still answers 401 regardless. Adding a router
would change the rendering mechanism, not the control.
