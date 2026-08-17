# Lab 35 evidence — screenshots to capture

Run the Spring CRM API on `:8080` and the SPA on `:5173`, then capture these in order.
Redact secrets. Items 9 and 10 need no backend; everything else needs Spring up.

| # | File | What it must show |
| - | ---- | ----------------- |
| 1 | `01-list-load.png` | DevTools Network: `GET /api/customers` 200 with the `X-Correlation-Id: lab-request-001` request header, and the list showing CUS-1001 Amina Khan and CUS-1002 Ravi Singh |
| 2 | `02-loading-state.png` | The "Loading customers…" state, easiest with Network throttling on |
| 3 | `03-empty-state.png` | "No customers yet." with a 200 `[]` response. Restart Spring with `-Dspring-boot.run.arguments=--northstar.crm.seed-fixtures=false` |
| 4 | `04-error-state.png` | Spring stopped: "Cannot reach the CRM service" alert plus the Retry button |
| 5 | `05-create-201.png` | Network: one `POST /api/customers` 201 and the new row in the list |
| 6 | `06-create-400.png` | Network: `POST` 400 with the `violations` body, and the message rendered beside the Email field |
| 7 | `07-double-click-save.png` | Network panel proving a double-clicked Save produced exactly one POST |
| 8 | `08-cors-evil-origin.png` | Terminal: `curl -i -H "Origin: https://evil.example" http://localhost:8080/api/customers` with no `Access-Control-Allow-Origin` for evil.example |
| 9 | `09-tests-green.png` | `npm run test -- --run` output, all 21 tests passing |
| 10 | `10-build-green.png` | `npm run build` output, `tsc -b && vite build` clean |
