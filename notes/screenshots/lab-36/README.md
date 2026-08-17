# Lab 36 evidence — screenshots to capture

Run the Spring CRM API on `:8080` with the **default** profile (no `lab35`) and the SPA on
`:5173`. Redact any `Authorization` header value before saving. Items 9 and 10 need no backend.

| # | File | What it must show |
| - | ---- | ----------------- |
| 1 | `01-login-guard.png` | The app at `http://localhost:5173` while signed out: the Sign in form, no customer data, and an empty Network panel for `/api/customers` |
| 2 | `02-anonymous-401.png` | Terminal: `curl -i http://localhost:8080/api/customers` with no token returning 401, proving the API, not the guard, is the control |
| 3 | `03-signed-in-list.png` | After signing in as `agent1`: Amina CUS-1001 and Ravi CUS-1002 listed, with the `/api/customers` request showing 200 |
| 4 | `04-bearer-header.png` | DevTools Network, the `/api/customers` request headers showing `Authorization: Bearer …` **redacted** and `X-Correlation-Id: lab-request-001` |
| 5 | `05-no-web-storage.png` | DevTools Application tab, Local Storage and Session Storage for `localhost:5173` both empty while signed in |
| 6 | `06-forbidden-403.png` | Signed in as `agent1`, after clicking "Check admin access": the 403 in Network and "You do not have access to that." on screen, with the customer list still visible |
| 7 | `07-generic-login-error.png` | A failed sign in with a wrong password showing exactly `Invalid username or password`, and the 401 response in Network whose body wording is not repeated on screen |
| 8 | `08-security-headers.png` | Terminal: `curl -I http://localhost:8080/api/customers` showing `Content-Security-Policy`, `Referrer-Policy: no-referrer`, `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY` |
| 9 | `09-tests-green.png` | `npm run test -- --run` output, all 15 tests passing |
| 10 | `10-build-green.png` | `npm run build` output, `tsc -b && vite build` clean |

Optional extra worth having: sign out, then press Back, and capture that the customer list does
not reappear without a fresh sign in.
