# Lab 44 — Release plan

## Immutable artifact

Promote **one** identity from Lab 43: **`jarSha256`** + **`gitCommit`** from `SHA256SUMS`. Never rebuild with Maven on the deploy agent. Image digest / GHCR is **optional** (Lab 41 `RepoDigests` is empty until you push).

| Field | Value |
| ----- | ----- |
| Version | 0.0.1 |
| Commit | d1f4ce39e8c9cb49c323143d1d84a03f9ccaafc5 (Lab 43 `commit=` line) |
| JAR SHA-256 | 1679689d647e2742f90e35fd09d3a35e470395f4d1782ae7098a024919c2b301 (Lab 43 `SHA256SUMS`, not a local `mvn package`) |
| Image digest | `null` unless you pushed |

## Promotion path

```text
Lab 43 CI package (crm-jar) → test → staging (list-API smoke) → [approval] → production
```

## Gates (objective)

| Env | Gate | Evidence |
| --- | ---- | -------- |
| test | Lab 43 verify green | CRM CI run on main (commit d1f4ce3) |
| staging | SHA match + `GET /api/customers?status=ACTIVE` as agent-a | readiness UP + 200 list, screenshot under notes/screenshots/lab-44/ |
| production | approval + `jarSha256` match | GitHub Environment required reviewer signs off |

## Config vs artifact

Env-specific ConfigMaps/Secrets stay outside the JAR: `SPRING_DATASOURCE_*` (host ITs) and Lab 42 `CRM_DB_*` (k3d), plus the Ingress host, are injected per environment. The Secret carries `CRM_DB_PASSWORD` and the agent passwords out-of-band. Only secret names live in Git. DB user is `crm`, not `crm_app`. No Kafka in this lab's smoke.

## DB compatibility

Expand-before-contract: an additive migration (like V2's `owner_agent`) is safe to roll a JAR back across; a `DROP COLUMN` is not, because a jarSha256 rollback cannot restore dropped data. Isolated DB is `crm_lab43`. Do not treat Lab 42's `crm_lab42` as production.
