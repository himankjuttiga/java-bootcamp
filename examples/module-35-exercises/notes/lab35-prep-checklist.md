# Lab 35 prep checklist

## Earlier exercise files present?

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/lab35-error-ux.md | yes |
| notes/lab35-fetch-flow.md | yes |
| notes/lab35-cors-and-headers.md | yes |
| notes/lab35-api.md | yes |
| notes/lab35-todos.md | yes |

## Fixtures (verify)

| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE |
| CUS-1002 | Ravi Singh | PROSPECT |

Correlation id for every lab call: `X-Correlation-Id: lab-request-001`.

## Entry gates

| Gate | State |
| ---- | ----- |
| Lab 34 CRM UI available (controlled form, props/state split) | yes, `examples/module-34-exercises/notes` complete |
| Spring CRM API reachable on `http://localhost:8080` | deferred to lab; instructor stub is the fallback if the backend is not up |
| Vite dev server origin `http://localhost:5173` in the CORS allowlist | planned in `notes/lab35-cors-and-headers.md`, applied in lab |
| JWT login screens | not started, parked for Lab 36; header slot kept injectable in `http()` |

Runtime is deferred: nothing in this pre-lab needs a live API. Unit tests can pass with `fetch` mocked while the backend is down, which is exactly why the api layer is a separate module.

## Evidence preview for the lab

| Evidence | Source |
| --- | --- |
| UI to HTTP endpoint map with status codes | `notes/lab35-api.md` |
| Filled fetch helper with correlation header | `notes/lab35-todos.md` |
| Load state machine with abort and empty copy | `notes/lab35-fetch-flow.md` |
| CORS allowlist and no-secrets rule | `notes/lab35-cors-and-headers.md` |
| User-facing error copy, 404 / network / 400 | `notes/lab35-error-ux.md` |

## Scope statement

Pre-lab only — prepare for lab; do not complete full Lab 35 now.

## Self mark

Overall prep: Pass
If Fail, revisit exercise(s): n/a
