# CI runbook outline

- Workflow path: `java-bootcamp/.github/workflows/crm-ci.yml`.
- Triggers and jobs: a PR runs `verify` only. A push to main or a tag `v*` runs `verify` then `package`. No deploy yet (Lab 44).
- Where evidence lives: `test-reports` artifact (Surefire) on every run, `crm-jar` artifact (JAR + SHA256SUMS) on main/tags.
- Re-run a failed verify:
  - GitHub UI: Actions, open the failed run, Re-run failed jobs. CLI: `gh run rerun <run-id> --failed`.
  - Reproduce locally from `examples/lab43-crm`: `mvn -B -ntp clean verify` (needs a local postgres, e.g. the crm-postgres container).
- Secret names only in this runbook: `NVD_API_KEY`, `CRM_REGISTRY_TOKEN` (Lab 44). Never the values. Redact any token that appears in evidence.

Scope: pre-lab outline only. The full runbook is Lab 43.

**Self-mark:** Pass
