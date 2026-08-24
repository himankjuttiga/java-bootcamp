# Lab 44 — Release checklist

## Go / No-Go

| # | Check | Go / No-Go |
| - | ----- | ---------- |
| 1 | Manifest `jarSha256` matches downloaded Lab 43 `crm-jar` (not a local rebuild) | Go once the SHA256SUMS value is pasted into the manifest |
| 2 | Staging smoke: readiness + `GET /api/customers?status=ACTIVE` + `lab-request-001` (or documented tabletop) | Go, verified on Lab 42 k3d (readiness UP, 200 list as agent-a) |
| 3 | Security gate residual risks accepted with owners (Lab 40) | Go, time-bounded suppressions lab40-004/005/006 (until 2026-11-01), owner recorded |
| 4 | Rollback runbook rehearsed; prior SHA / Image Id recorded **before** promote | Go, first release so prior is "none"; Lab 42 `rollout undo` rehearsed |
| 5 | No secrets in Git, manifest, or release notes | Go, manifest and CD carry names only |
| 6 | `crm-cd.yml` is at the **git root**; no `mvn` in CD | Go, root workflow, promote-only |

## Decision

- **Decision:** GO (tabletop, first release) once the manifest `jarSha256` is filled from the downloaded SHA256SUMS
- **Approver:** instructor sign-off before the prod promote
- **Date/time:** 2026-08-24
- **Rationale:** identity is the immutable Lab 43 `crm-jar` (no rebuild), staging smoke is green, rollback is documented, and no secrets are in Git.
