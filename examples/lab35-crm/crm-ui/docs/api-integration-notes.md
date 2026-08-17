# Lab 35 — API integration notes

## Request flow

```
UI event (click / submit)
  -> App handler                     src/App.tsx
  -> useCustomers                    src/hooks/useCustomers.ts   (request state + abort + saving guard)
  -> customersApi                    src/api/customers.ts        (paths, DTO shape, field aliases)
  -> http<T>()                       src/api/http.ts             (base URL, headers, 204, non-OK -> ApiError)
  -> fetch                           browser
  -> Spring Boot CRM API :8080       /api/customers
  -> ApiError (on failure)           src/api/ApiError.ts
  -> UI state: loading | data | empty | error
```

No component calls `fetch` directly. That is the whole point of the boundary: Lab 36 adds
`Authorization` in `http.ts` once instead of in every screen.

## Contract as observed

| UI action | HTTP | Success | Failure classes handled |
| --- | --- | --- | --- |
| Load list | `GET /api/customers` | 200 `Customer[]` | 500, network, abort |
| Open one | `GET /api/customers/{id}` | 200 `Customer` | 404, 500, network |
| Create | `POST /api/customers` | 201 `Customer` | 400 field errors, 409 duplicate, 500 |
| Update | `PUT /api/customers/{id}` | 200 `Customer` or 204 no body | 400, 404, 500 |

Fixtures: Amina `CUS-1001` `ACTIVE`, Ravi `CUS-1002` `PROSPECT`. Correlation header
`X-Correlation-Id: lab-request-001` is sent on every request, reads included.

### Shape mismatch, normalised in one place

The Spring CRM model (Lab 29 onward) serialises `id` / `name`; the SPA types use
`customerId` / `fullName`. `toCustomer()` in `src/api/customers.ts` accepts either shape,
so the mismatch never reaches a component. Validation violations are translated the same
way, `name` -> `fullName` and `id` -> `customerId`, by `FIELD_ALIASES`.

Server error envelope consumed by `ApiError.from`:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "correlationId": "lab-request-001",
  "violations": [{ "field": "email", "message": "must be a well-formed email address" }]
}
```

## Base URL and env

`VITE_API_BASE_URL=http://localhost:8080` lives in `.env` (gitignored), with `.env.example`
committed. `http.ts` strips trailing slashes and every path carries its own `/api` prefix,
which is what prevents the classic `/api/api/customers` 404. Vite inlines env values at
dev-server start, so a `.env` edit needs a Vite restart.

Never put secrets in `VITE_*`: those values are compiled into the browser bundle and readable
in DevTools. Only the public API host belongs there. DB passwords, Kafka credentials and
signing keys stay in Spring config.

## CORS

| Piece | Origin |
| --- | --- |
| Vite dev server (UI) | `http://localhost:5173` |
| Spring CRM API | `http://localhost:8080` |

Different port means a different origin, so every browser call is cross-origin and Spring
must answer with `Access-Control-Allow-Origin: http://localhost:5173`. The allowlist is
explicit, never `*`, and `X-Correlation-Id` must appear in `allowedHeaders` or the preflight
fails before the real request is sent. Backend config and the hostile-origin probe live in
`../../docs/cors-backend-notes.md`.

A CORS error in the browser while `curl` succeeds is never an outage: `curl` sends no
`Origin` and enforces no same-origin policy, so the fault is in the allowlist or the port.

## Request states

| State | Trigger | UI |
| --- | --- | --- |
| loading | mount or Retry | `role="status"` "Loading customers…" |
| data (rows) | 200 with records | list of `customerId — fullName — status` |
| data (empty) | 200 with `[]` | "No customers yet." and no error |
| error | network, 4xx, 5xx | `role="alert"` plus a Retry button |

`empty` is deliberately not a separate kind: it is `data` with `length === 0`. Treating an
empty list as an error, or an outage as an empty list, is the failure this table prevents.

## Abort and duplicate submits

`useCustomers` creates an `AbortController` per load and aborts it in the effect cleanup, so
an unmount or a Retry cancels the obsolete request instead of applying a stale response.
`ApiError` with `kind === 'abort'` is swallowed: a cancellation we caused is not a failure and
gets no error UI.

Writes are guarded by `savingRef`, a ref rather than state, because the guard has to flip
synchronously on the first click, before React re-renders with the disabled button. A second
submit while a save is in flight returns `null` and sends no second POST.

## Error copy shown to the user

| Case | User sees | Logs keep |
| --- | --- | --- |
| Network down | "Cannot reach the CRM service" | `TypeError: Failed to fetch` |
| 400 | the server's field message beside the field | violation list, correlation id |
| 404 | "That customer could not be found." | requested id |
| 500 | "The CRM service had a problem. Please try again." | full stack trace, correlation id |
| Non-JSON body (proxy HTML) | the same safe 500 copy | discarded, never echoed |
| Abort | nothing | nothing |

Stack traces, SQL and raw exception text never reach the screen. The correlation id is the
only technical detail worth surfacing, and only as a reference the user can quote.

## Tests

`src/api/customers.test.ts` covers 200 list (both server shapes), empty list, 201 create,
400 with field-alias mapping, 404, 500, non-JSON 500 body, network failure, abort, signal
pass-through, 204 update, and the `/api/api` join guard. `src/App.test.tsx` covers loading,
data, empty, error plus Retry, abort on unmount, 400 mapped onto the Email field, successful
create using the server record, single POST for a double-clicked Save, and a PUT update.
`fetch` is always stubbed with `vi.stubGlobal`; no test touches the network.
