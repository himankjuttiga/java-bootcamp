# ci.yml TODOs

- Live file: `java-bootcamp/.github/workflows/crm-ci.yml`. A nested `examples/lab43-crm/.github/` never runs, GitHub only reads workflows at the repo root.
- `on:` `pull_request` plus `push:` with `branches: [main]` and `tags: ["v*"]`.
- `defaults.run.working-directory: examples/lab43-crm` so every step runs inside the CRM project.
- `jobs.verify`: `mvn -B -ntp clean verify`. Lab 41 has no mvnw, so plain `mvn`, and no `-DskipTests` on verify. Needs a postgres:16 service plus `SPRING_DATASOURCE_*` env pointing at `localhost:5432/crm_lab43`.
- `jobs.package`: `needs: verify`, `if:` main or tag `v*`. Builds the JAR once and uploads it.

Scope: mapping the TODOs only. The graded file is Lab 43.
