# Rollout / rollback

1. `kubectl rollout status deploy/crm-api -n <ns>` → pods Ready, then Ingress HTTP 200
2. Introduce a bad revision: point `image` at a non-existent digest → pods never become Ready
3. `kubectl rollout undo deploy/crm-api -n <ns>` → back to the known-good digest
4. Re-smoke: readiness UP, then GET `CUS-1001` with header `X-Correlation-Id: lab-request-001`

Evidence: before/after under `notes/screenshots/lab-42/`.
