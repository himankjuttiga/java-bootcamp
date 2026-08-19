# Lab 39 prep checklist

## Earlier exercise files present?

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/lab39-jpa.md | yes |
| notes/lab39-repository-sketch.md | yes |
| notes/lab39-todos.md | yes |
| notes/lab39-paging-locking.md | yes |
| notes/lab39-flyway-plan.md | yes |

## Fixtures (verify)

| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE |
| CUS-1002 | Ravi Singh | PROSPECT |

Ravi keeps zero accounts, so `findByCustomerId` must return an empty list rather than throwing.
Correlation id `lab-request-001` continues from Lab 35 through the API logs into
`customer_status_history`.

## Entry gates

| Gate | State |
| ---- | ----- |
| PostgreSQL 16 running with a persistent volume | yes, `examples/lab37-crm/compose.yaml`, container `crm-postgres` |
| Lab 37 schema and constraints understood | yes, four tables, 14 named constraints |
| Lab 38 index evidence available | yes, `examples/lab38-crm/database/performance/report.md` |
| Maven and JDK 21 | yes, confirmed while running Lab 29 |
| Docker for Testcontainers | yes, Docker Desktop engine running |

## Decisions carried into the lab

| # | Decision | Rationale |
| - | -------- | --------- |
| 1 | `ddl-auto: validate`, Flyway owns the schema | one owner for DDL. Validate turns a mapping drift into a boot failure instead of a runtime surprise |
| 2 | `open-in-view: false`, DTOs at the controller boundary | lazy loads stay inside the transaction; entities never serialise, so schema and API can evolve apart |
| 3 | `@Version` on customer and account, both conflict types mapped to 409 | duplicate key and stale version are different causes with the same honest answer: reload and reapply |

## Deferred

| Deferred | Where it belongs |
| --- | --- |
| Running Boot, Flyway or Testcontainers | the graded lab, not the pre-lab |
| `ddl-auto: create` or `update` as a long-term setting | never in a shared environment |
| H2 in PostgreSQL compatibility mode for integration tests | never; Testcontainers with the real engine |
| Lab 40 SAST work | Week 5 |

## Known discrepancies to resolve in the lab

1. **Id type.** The exercise templates use `@Id String customerId` and
   `JpaRepository<CustomerEntity, String>`. The Lab 37 DDL and the Lab 39 starter both use a
   `BIGSERIAL` surrogate, so the id is `Long` and the business key is `publicId`. The notes follow
   the schema that actually exists.
2. **`version` column.** The Lab 37 schema has none. `V1__crm_schema.sql` in the Lab 39 starter
   adds it, which is why the lab runs against its own migration rather than the Lab 37 database.
3. **Lab 38 indexes.** Justified indexes should arrive as `V2__performance_indexes.sql` rather than
   being created by hand, or two machines end up with different schemas.

Can verify pass without a real PostgreSQL? No. `ddl-auto: validate` needs the actual schema, Flyway
needs the real history table, and the 409 paths depend on PostgreSQL SQLSTATE 23505. An H2 run
would go green while proving none of it.

## Scope statement

Pre-lab only — prepare for lab; do not complete full Lab 39 now.

## Self mark

Overall prep: Pass
If Fail, revisit exercise(s): n/a
