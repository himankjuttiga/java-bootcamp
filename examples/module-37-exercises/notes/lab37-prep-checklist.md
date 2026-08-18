# Lab 37 prep checklist

## Earlier exercise files present?

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/lab37-design.md | yes |
| notes/lab37-er-sketch.md | yes |
| notes/lab37-constraints.md | yes |
| notes/lab37-ddl-todos.md | yes (SQL also in `notes/lab37-todos.sql`) |
| notes/lab37-seed-and-verify-plan.md | yes |

## Fixtures (verify)

| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE |
| CUS-1002 | Ravi Singh | PROSPECT |

Ravi has no account on purpose, which is what makes the customer to account relationship
`1 : 0..N` rather than `1 : 1..N`. Status history may carry correlation `lab-request-001`.

## Decisions carried into the lab

| # | Decision | Rationale |
| - | -------- | --------- |
| 1 | Surrogate `BIGSERIAL` PK plus a `public_id` business key | email and public ids are mutable or reissuable; a surrogate never changes, so foreign keys never need rewriting |
| 2 | `BIGINT` cents for money, `TIMESTAMPTZ` for time | binary floats cannot represent 0.10 exactly; a bare `TIMESTAMP` loses the offset and makes history ambiguous |
| 3 | Named constraints, and an explicit index on every FK column | named constraints let the API map a violation to a field, and PostgreSQL does not index the referencing side of a foreign key for you |

Cascade policy: `RESTRICT` from `account` and `customer_status_history` to `customer`, `CASCADE`
from `address`. Closing a customer is a status change, never a row delete.

## Object ownership

The lab's `01_create_user.sql` creates `crm_app` with `LOGIN` and limited grants. Objects should be
owned by an admin or migration role, with `crm_app` granted only `SELECT`, `INSERT`, `UPDATE` and
`DELETE` on the tables it uses. If the application role owns the schema it can drop it, which turns
one injected statement or one bad migration into data loss. `crm_app` is never `SUPERUSER`.

## Deferred

| Deferred | Where it belongs |
| --- | --- |
| JPA entities and repositories | Lab 39 |
| `EXPLAIN` plans and index tuning | Lab 38 |
| Kafka outbox tables | not this module |
| Running Docker or applying any DDL | the graded lab, not the pre-lab |

## Runtime plan

Shared instructor PostgreSQL is the preferred path, with host, port `5432`, database and
credentials recorded in a gitignored `.env`. Local Docker is the fallback if the instructor allows
it: the starter `compose.yaml` pins `postgres:16` while the guide's `docker run` fallback uses
`postgres:17`. Pick one and stay on it, because a version 16 data directory will not start under
17 against the same named volume.

Nothing has been started yet. Docker readiness is being checked separately, before class rather
than during it.

## Scope statement

Pre-lab only — prepare for lab; do not complete full Lab 37 now.

## Self mark

Overall prep: Pass
If Fail, revisit exercise(s): n/a
