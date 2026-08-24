# Rollback runbook

1. Before promote, record the known-good `jarSha256` (and Image Id if the image was pushed) of what is currently in prod.
2. Redeploy the prior artifact by its jarSha256. Download it, do not `mvn package` again.
3. Verify recovery: readiness UP, then GET /api/customers?status=ACTIVE returns 200 (Host header on :8088 if live on k3d).
4. Record incident notes: what shipped, what broke, who approved, prior and new SHA, timeline.

**Self-mark:** Pass
