# Lab 35 — Integrating React with the Spring CRM API

Module 35 · Checkpoint **E** · copied from the course starter into
`examples/lab35-crm`, then completed.

The React CRM from Lab 34 no longer owns its data. Component state is now a cache of Spring
records, reached through one typed HTTP boundary with honest loading, empty and error states.

## Runbook

### 1. Start the Spring CRM API (:8080)

```bash
cd ~/java-bootcamp/examples/lab29-crm
mvn spring-boot:run -Dspring-boot.run.profiles=lab35
```

There is no Maven wrapper in that project, so use the installed `mvn`. The `lab35` profile
matters: Lab 29 locks `/api/customers/**` behind a Bearer token, and Lab 35 has no login UI
yet. The profile activates a second `SecurityFilterChain` that permits customer endpoints,
while the default profile keeps the Lab 29 role rules and its no-token 401 test green.

Lab 35 additions made to `examples/lab29-crm`:

| Change | File | Why |
| --- | --- | --- |
| `GET /api/customers` list, `PUT /api/customers/{id}` | `api/CustomerController.java`, `service/CustomerService.java` | the SPA loads a list and saves edits; Lab 29 only had POST and GET by id |
| CORS allowlist for `http://localhost:5173` | `config/WebConfig.java` (new) | a `CorsConfigurationSource` bean, consumed by Spring Security via `http.cors(...)`, so it applies to the filter chain and not only to MVC |
| `lab35` profile filter chain | `config/SecurityConfig.java` | scoped, reversible permit for customer endpoints until Lab 36 restores `hasAnyRole("AGENT","ADMIN")` |
| `northstar.crm.seed-fixtures` switch | `service/CustomerService.java` | demonstrate the SPA empty state without editing code; defaults to `true` |

To serve an empty list for the empty-state evidence:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=lab35 \
  -Dspring-boot.run.arguments=--northstar.crm.seed-fixtures=false
```

Verify the API before touching the UI:

```bash
curl -i http://localhost:8080/api/customers
```

### 2. Start the SPA (:5173)

```bash
cd ~/java-bootcamp/examples/lab35-crm/crm-ui
cp .env.example .env          # already present locally; .env is gitignored
npm install
npm run dev                   # http://localhost:5173
```

`.env` holds only the public host: `VITE_API_BASE_URL=http://localhost:8080`. Restart Vite
after any `.env` change, since values are inlined at server start. Never put secrets in
`VITE_*` variables.

### 3. Tests and build

```bash
cd ~/java-bootcamp/examples/lab35-crm/crm-ui
npm run test -- --run
npm run build
```

No test touches the network: `fetch` is stubbed with `vi.stubGlobal` in every case.

## What was built

| Deliverable | Where |
| --- | --- |
| Typed `ApiError` with kind, status, fieldErrors, correlationId | `crm-ui/src/api/ApiError.ts` |
| Fetch boundary: base URL join, correlation header, 204 guard, non-OK translation | `crm-ui/src/api/http.ts` |
| `customersApi` list / get / create / update plus DTO and field-name normalisation | `crm-ui/src/api/customers.ts` |
| Abortable load, request state machine, synchronous duplicate-submit guard | `crm-ui/src/hooks/useCustomers.ts` |
| Distinct loading / data / empty / error UX with Retry | `crm-ui/src/App.tsx` |
| Backend 400 mapped to the offending form field | `crm-ui/src/App.tsx`, `crm-ui/src/api/customers.ts` |
| Response-class tests (200/201/400/404/500/network/abort/204) | `crm-ui/src/api/customers.test.ts` |
| UI state and write-flow tests | `crm-ui/src/App.test.tsx` |
| Integration notes | `crm-ui/docs/api-integration-notes.md` |
| CORS allowlist, preflight and hostile-origin probe | `docs/cors-backend-notes.md`, `docs/backend/WebConfig.java` |

Design decisions worth remembering:

* **One boundary.** Components never call `fetch`, so Lab 36 attaches `Authorization` in a
  single file.
* **Empty is not an error.** `data` with `length === 0` renders an empty state; an outage
  renders an alert with Retry. Collapsing the two hides outages.
* **Abort is not a failure.** `ApiError.kind === 'abort'` is swallowed, so a cancelled load
  produces no toast and no `setState` after unmount.
* **The saving guard is a ref, not state.** It must flip synchronously on the first click,
  before React re-renders the disabled button, or a fast double-click sends two POSTs.
* **Contract drift is absorbed once.** Spring serialises `id` / `name`; the SPA uses
  `customerId` / `fullName`. `toCustomer()` and `FIELD_ALIASES` translate both records and
  validation violations in `customers.ts`.
* **Nothing raw reaches the screen.** A non-JSON error body is discarded rather than echoed;
  the correlation id is the only technical detail ever surfaced.

## Checkpoints

### Checkpoint A — Tooling

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | `lab35-crm/crm-ui` from the Module 35 starter, Lab 34 flows carried over | Pass |
| 2 | Spring API reachable; contract documented via curl | API running on `:8080` with the `lab35` profile; curl output still to be pasted into `docs/cors-backend-notes.md` |
| 3 | `.env.example` with `VITE_API_BASE_URL`, no secrets | Pass |

### Checkpoint B — Client core

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | `ApiError` + `http` helper + `customersApi` | Pass |
| 2 | Abortable list load; distinct UI states | Pass |
| 3 | Create/update with correlation header | Pass |
| 4 | 400 field errors mapped; saving disables duplicate POST | Pass |

### Checkpoint C — CORS + tests

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | Spring CORS allowlist for the Vite origin | Pass — installed in `examples/lab29-crm/config/WebConfig.java`, reference copy kept in `docs/backend/WebConfig.java` |
| 2 | Evil Origin probe recorded | Pending — probe command ready; paste output under "Recorded output" |
| 3 | Response-class tests green twice; build green | Pass — 21 tests green on two consecutive runs, `npm run build` green |

### Checkpoint D — Hygiene

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | Integration notes + screenshots | Notes Pass; screenshots pending, see `notes/screenshots/lab-35/README.md` |
| 2 | No secrets / `node_modules` / `dist` / `.env` committed | Pass — `.gitignore` covers `node_modules/`, `dist/`, `.env`, `.idea/` |
| 3 | README runbook starts Spring + Vite | Pass — this file |

## Failure experiments

| # | Experiment | Observed | Restore |
| - | ---------- | -------- | ------- |
| 1 | Transport failure on load | `ApiError` kind `network`, alert "Cannot reach the CRM service" plus Retry; covered by test | Retry re-runs the load |
| 2 | POST invalid email | 400 `violations` mapped to the Email field, form stays open, cached list unchanged; covered by test | Fix the payload |
| 3 | Double-click Save | Exactly one POST, button reads "Saving…" and is disabled; covered by test | Keep the `savingRef` guard |
| 4 | Unmount mid-load | Signal aborted, no error UI, no `setState` warning; covered by test | Keep the effect cleanup |
| 5 | Non-JSON 500 body | Safe copy shown, HTML body discarded; covered by test | n/a |
| 6 | Evil Origin curl | Pending live run — expect no `Access-Control-Allow-Origin` for `evil.example` | Keep the allowlist |

## Security and production review

* **Untrusted inputs:** every browser payload, and the `Origin` header itself. Client
  validation is a courtesy; Spring `@NotBlank` / `@Email` is the enforcement point.
* **Where enforcement lives:** validation server-side now, authentication and authorisation in
  Lab 36. `http.ts` keeps a single header slot so tokens attach in one place.
* **Sensitive values:** database, Kafka and signing credentials never appear in `VITE_*` or in
  the repo. `.env` is gitignored, `.env.example` carries only the public host.

## Reflection

1. **Which design decision most affected correctness?** Making the list a `RequestState`
   union instead of a bare array. Once loading, data, empty and error are separate cases, the
   UI cannot silently present an outage as "no customers".
2. **What evidence proves it works?** 21 tests green on two consecutive runs across every
   response class (200, 201, 400, 404, 500, non-JSON body, network, abort, 204) plus a green
   `tsc -b && vite build`. Live-API curl and screenshot evidence is the remaining piece.
3. **Which failure was hardest to diagnose?** The duplicate submit. A `saving` state flag
   looks correct but updates asynchronously, so a fast second click lands before the button is
   disabled. Only the synchronous ref guard actually prevents the second POST.
