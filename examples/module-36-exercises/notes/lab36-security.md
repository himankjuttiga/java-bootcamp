# Lab 36 — Threat Sketch

## Step 1 — Assets

What an attacker wants out of the CRM SPA:

| Asset | Why it is worth stealing |
| --- | --- |
| Access token | Replays as the logged-in agent against every `/api/customers` endpoint |
| Customer PII (Amina `CUS-1001`, Ravi `CUS-1002`) | Names, emails and status are regulated data, not test rows |
| Admin actions | `/api/admin/**` is role gated; a stolen ADMIN token escalates instantly |
| Correlation and log data | Reveals internal ids and request flow, useful for probing |

## Step 2 — Threats

| Threat | How it lands here | First defence |
| --- | --- | --- |
| XSS | A customer name rendered as HTML executes attacker script inside our origin | Render as text, never `dangerouslySetInnerHTML` |
| Token theft | Script running in our origin reads the token and exfiltrates it | Keep the token in memory, never in Web Storage |
| CSRF | Only in scope if auth rides on a cookie the browser attaches automatically | Bearer header from JS, plus `SameSite` if cookies are ever used |
| Over-trusting UI guards | Hiding a route or a button and calling it access control | Spring Security enforces on every request |
| Open redirect after login | A `returnTo` parameter pointing off-origin sends the user, and any token in the URL, to an attacker | Allow relative in-app paths only |
| Secrets in the bundle | An API key placed in `VITE_*` ships to every browser | Only the public API base URL belongs in Vite env |

## Step 3 — UI vs API

Hiding a button is not authorization. `ProtectedRoute` is user experience: it stops an unauthenticated
person from staring at an empty screen. Anyone can bypass it by typing a URL, editing React state in
devtools, or skipping the SPA entirely and calling `http://localhost:8080/api/customers` with curl.
The only real gate is Spring Security, which rejects the request whether or not our UI ever rendered.

If `ProtectedRoute` is bypassed by URL, the data is still safe, because the fetch behind that route
carries no valid token and the API answers 401. The screen may look broken; the data does not leak.
That is the correct failure mode, and it is why Lab 36 restores
`.requestMatchers("/api/customers/**").hasAnyRole("AGENT","ADMIN")` in place of the temporary Lab 35
`permitAll`.

Hardcoding an API key into Vite env is the same mistake in another costume: `VITE_*` values are
inlined into the client bundle, so the "secret" is one devtools panel away from any visitor.

## Step 4 — Notes

Saved in `notes/lab36-security.md`. No real tokens or credentials appear anywhere in these notes;
the placeholder is `lab-token-001`.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
