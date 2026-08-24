# Lab 44 — Rollback runbook

## Known-good identity

| Field | Value |
| ----- | ----- |
| Previous `jarSha256` | none (first release, no prior prod artifact); record the outgoing SHA before every future promote |
| Previous version / Image Id | Lab 42 k3d image `crm-api:lab41`, Id `sha256:cb27c69bdb37a9e36dde3f64fa3f6138f1ffe28031340e25bdb0bb5b312f6a2e` |
| Verification check | readiness + **`GET /api/customers?status=ACTIVE`** with `X-Correlation-Id: lab-request-001` |

There is **no** `GET /api/customers/{id}`. Optional local cluster is **Lab 42 k3d** (`Host` header on `:8088`), not instructor GHCR.

## Procedure (sketch)

1. Announce incident / change freeze as needed (Lab 47 templates).
2. Redeploy the **prior** identity — do **not** `mvn package`. For optional k3d: `kubectl -n crm-training rollout undo deployment/crm-api`.
3. For Lab 42 k3d: `kubectl -n crm-training rollout undo deployment/crm-api` then `kubectl -n crm-training rollout status deployment/crm-api --timeout=180s`. For a JAR-only host: stop the service, redeploy the prior `crm-jar` by its jarSha256 from the artifact store, restart. Tabletop is valid on the timed path since there is no prior prod release yet.
4. Verify readiness + list-API smoke (Host header on `:8088` if live on k3d).
5. Record outcome in release notes (no secrets).

## Rehearsal evidence

`notes/screenshots/lab-44/rollback.png` (redact tokens). The Lab 42 `rollout undo` rehearsal already exercised this path on the same k3d deployment.
