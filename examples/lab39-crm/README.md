# Lab 39 — Spring Data JPA and PostgreSQL

Module 39 · Checkpoint **E** · course starter copied into `examples/lab39-crm` and completed.

## Runbook

Required environment variables, all read from `.env` (gitignored). `.env.example` holds the keys
with placeholder values; nothing in this repository contains a working password.

| Variable | Purpose |
| --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/crm` |
| `SPRING_DATASOURCE_USERNAME` | application user |
| `SPRING_DATASOURCE_PASSWORD` | no default anywhere; startup fails without it |
| `POSTGRES_USER` / `POSTGRES_PASSWORD` / `POSTGRES_DB` | consumed by `compose.yaml` |

```bash
cd ~/java-bootcamp/examples/lab39-crm
cp .env.example .env          # then set lab-only values
set -a; source .env; set +a   # every command below needs these exported
docker compose up -d
docker compose logs --tail 5 postgres   # wait for "ready to accept connections"

mvn spring-boot:run
mvn test -Dtest=CustomerRepositoryIT
mvn clean verify              # Failsafe runs *IT
```

```bash
curl -s localhost:8080/api/customers?status=ACTIVE | jq
curl -s -X POST localhost:8080/api/customers \
  -H 'Content-Type: application/json' -H 'X-Correlation-Id: lab-request-001' \
  -d '{"publicId":"CUS-1001","fullName":"Amina Khan","email":"amina@example.com","status":"ACTIVE"}'
```

**Flyway.** `V1__crm_schema.sql` applies once on first start and Hibernate is `ddl-auto: validate`,
so a mapping that disagrees with the migration stops the boot. Never edit an applied migration:
that is a checksum mismatch at startup, and the forward fix is `V2__*.sql`. `CustomerRepositoryIT`
runs against the compose PostgreSQL 16 container, not H2, and reads `SPRING_DATASOURCE_*` from the
environment, so export them in that shell first. Concepts and design decisions are in
`docs/jpa-postgres-notes.md`.

## Deliverables

| # | Deliverable | Where |
| - | ----------- | ----- |
| 1 | Boot app with JPA, PostgreSQL and Flyway `V1` | `pom.xml`, `src/main/resources/application.yml`, `db/migration/V1__crm_schema.sql` |
| 2 | Entities with correct types and `@Version` | `customer/CustomerEntity.java`, `account/AccountEntity.java` |
| 3 | Repositories, transactional service, bounded paging controller | `customer/`, `account/` |
| 4 | Duplicate and optimistic conflicts mapped to 409 | `api/ApiExceptionHandler.java` |
| 5 | `CustomerRepositoryIT` + `mvn clean verify` evidence | `src/test/java/...`, `screenshots/` |
| 6 | `.env.example` + README runbook | `.env.example`, this file |
| 7 | Concepts notes, no secrets committed | `docs/jpa-postgres-notes.md`, `.gitignore` |

## Checkpoints

### Checkpoint A — Tooling

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | `lab39-crm` under `examples/` | Pass |
| 2 | PostgreSQL healthy; JPA, driver and Flyway on the classpath | Pass |
| 3 | Env-based credentials, `.env` gitignored | Pass |

### Checkpoint B — Schema and entities

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | Flyway `V1` applied, `ddl-auto: validate` | Pass — `screenshots/hibernate.png`, `screenshots/validate-start-crm.png` |
| 2 | Entities mapped, `@Version` present | Pass |
| 3 | Lazy collections excluded from equality and JSON | Pass — no mapped collections, DTOs only |

### Checkpoint C — API and persistence behaviour

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | Repositories: publicId, email exists, status paging | Pass |
| 2 | Transactional create/find, bounded deterministic paging | Pass — cap 100, allow-listed sort, id tie-breaker: `screenshots/paging-deterministic.png`, `screenshots/paging-bounds.png` |
| 3 | 409 for duplicate and optimistic lock, no SQL leaked | Pass — `screenshots/duplicate-409.png`, `screenshots/optimistic-409.png` |

### Checkpoint D — Hygiene

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | `CustomerRepositoryIT` and `mvn clean verify` green | Pass — 8 tests: `screenshots/IT-green.png`, `screenshots/verify-green.png` |
| 2 | README runbook complete | Pass |
| 3 | No secrets or `target/` committed | Pass |

## Failure experiments

| # | Experiment | Observed | Restore |
| - | ---------- | -------- | ------- |
| 1 | POST the same email twice | 409 with `correlationId`, no SQL or constraint name | unique constraint stays |
| 2 | Two writers update one customer | second raises `OptimisticLockingFailureException` → 409 | keep `@Version` |
| 3 | `?sort=password` | 400, allow-list rejects it | keep the allow-list |
| 4 | `?size=1000000` | capped at 100 rows | keep the cap |
| 5 | Edit `V1` after it applied | Flyway checksum mismatch at startup | write `V2` instead |

## Reflection

1. **Which design decision most affected correctness?** Letting the unique constraint be the
   guarantee rather than an `existsByEmail` pre-check, which cannot close the race between two
   concurrent requests.
2. **What evidence proves the mappings work?** Eight integration tests against real PostgreSQL 16,
   covering the round trip, both constraint violations, paging across two pages and a lost-update
   race, plus `ddl-auto: validate` failing the boot on any mapping drift.
3. **Which failure was hardest to diagnose?** Validation on a `@RequestParam` throws
   `ConstraintViolationException`, which the advice does not handle, so a bad status returned 500
   instead of 400. Moving the check into the service fixed the failure mode.
