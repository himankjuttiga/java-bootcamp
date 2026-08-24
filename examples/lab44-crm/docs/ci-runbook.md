# Lab 43 — CI runbook

## Two folders

Work in **`java-bootcamp`**. Workflow file: **`.github/workflows/crm-ci.yml`** at the repo root (not under `examples/lab43-crm/.github/`).

## Pipeline policy

| Trigger | Jobs | Notes |
| ------- | ---- | ----- |
| Pull request | verify | Fast feedback; no `crm-jar` |
| `main` push | verify + package | Immutable JAR + checksum |
| Tag `v*` | verify + package | Release candidate identity |
| Deploy | none | Lab 44 |

## Secrets / variables

Names only, never values:

- `NVD_API_KEY` — Actions secret, optional `-Psecurity-scan` dependency-check (the Lab 40 key). Referenced as `${{ secrets.NVD_API_KEY }}`.
- Registry token / kubeconfig — not yet, they arrive in Lab 44.
- The CI `postgres:16` service uses a throwaway `change-me`, an ephemeral service password, not a real credential.

## Re-run failed verify

1. Open Actions → failed run
2. If it was a flake (e.g. the postgres service was not ready), use Re-run failed jobs (`gh run rerun <run-id> --failed`). If a test genuinely failed, fix it locally and push, do not just re-run.
3. Confirm Surefire artifact uploaded (`if: always()`)
4. Local equivalent: `mvn -B -ntp clean verify` from `examples/lab43-crm`

## Failure experiment (safe)

Break one real assertion in `ObjectOwnershipSecurityTest` (not `anonymousReadIs401`, this CRM authenticates every `/api` route), push on a branch, and open a PR. Verify goes red, the Surefire `test-reports` artifact still uploads (`if: always()`). Restore the assertion, push again, verify goes green.

## Artifact identity for Lab 44

- JAR + `SHA256SUMS` + `GITHUB_SHA` from artifact **`crm-jar`**
- Produced on `main` / `v*` only
- Lab 44 must **download** this artifact — do not `mvn package` again
