# Lab 35 — CORS and Headers

## Step 1 — Origins

| Piece | Origin |
| --- | --- |
| React UI (Vite dev server) | `http://localhost:5173` |
| Spring CRM API | `http://localhost:8080` |

Different port means a different origin, so every browser call from the UI to the API is cross-origin even though both run on this laptop.

## Step 2 — CORS

The browser blocks cross-origin XHR and `fetch` responses unless the Spring API answers with `Access-Control-Allow-Origin` naming the UI origin.

Allowlist plan, exact strings only:

```java
registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:5173")
        .allowedMethods("GET", "POST", "PUT", "PATCH")
        .allowedHeaders("Content-Type", "X-Correlation-Id");
```

A wildcard `*` in production notes would let any site, including an attacker page, read authenticated CRM responses from a logged-in browser, and it cannot be combined with credentials at all. Keep the allowlist explicit per environment.

Debug note: a CORS error while `curl` succeeds is never a backend outage. `curl` sends no `Origin` header and enforces no same-origin policy, so the failure is in the Spring CORS config or the origin string, not the endpoint. Check the preflight `OPTIONS` response headers in the network tab first, and confirm the port is `5173` exactly rather than `3000` or `127.0.0.1`.

## Step 3 — Correlation

Send `X-Correlation-Id: lab-request-001` on fetches, mutations above all, so a UI action can be traced to its server log line. The header must appear in `allowedHeaders`, otherwise the preflight fails before the real request is sent.

Authorization headers wait for Lab 36. The `http()` helper keeps a single header-building spot so a bearer token can be injected later without touching components.

## Step 4 — Secrets

Only the public API base URL belongs in the frontend env: `VITE_CRM_API_URL=http://localhost:8080`.

Anything prefixed `VITE_` is inlined into the JavaScript bundle and readable by anyone with browser devtools. DB passwords, Kafka credentials, and signing keys stay server-side in Spring config, never in `.env` files that Vite reads.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
