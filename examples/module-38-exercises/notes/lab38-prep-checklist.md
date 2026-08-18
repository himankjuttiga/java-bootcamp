# Lab 38 prep checklist

## Earlier exercise files present?

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/lab38-perf.md | yes |
| notes/lab38-index-tradeoffs.md | yes |
| notes/lab38-sql-index-todos.md | yes (SQL also in `notes/lab38-todos.sql`) |
| notes/lab38-explain-checklist.md | yes |
| notes/lab38-sargability.md | yes |

## Fixtures (verify)

| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE |
| CUS-1002 | Ravi Singh | PROSPECT |

Both must survive the 50k-row generation in `01_generate_data.sql`. The generated rows get their
own `public_id` range so the fixtures stay findable, and so the email lookup has a known address
to seek.

## Entry gates

| Gate | State |
| ---- | ----- |
| Lab 37 schema applied and seeded | yes, `examples/lab37-crm/database`, four tables, 14 named constraints, verified end to end |
| PostgreSQL 16 container with a persistent volume | yes, `compose.yaml`, volume `lab37-crm_crm_pgdata` |
| psql available | through the container: `docker compose exec -T postgres psql -U crm_app -d crm` |
| Volume data generated | no, that happens in the lab |

## Decisions carried into the lab

| # | Decision | Rationale |
| - | -------- | --------- |
| 1 | Measure before indexing, and measure again after | an index with no before-and-after plan is a guess with a write cost. `EXPLAIN (ANALYZE, BUFFERS)`, second run, median of a few |
| 2 | Composite `(status, created_at, customer_id)` rather than an index on `status` alone | status is low selectivity; the composite serves the filter and the sort together, so no separate sort node |
| 3 | Keyset pagination over deep `OFFSET` | `OFFSET 100000` builds and discards 100000 rows; a row-value seek costs the same on page 5000 as on page 1 |

Can I pass without a before plan? No. The whole point of the lab is the comparison, so a fast
query with no baseline proves nothing about the index that supposedly made it fast.

## Deferred

| Deferred | Where it belongs |
| --- | --- |
| JPA entities, repositories, Flyway | Lab 39 |
| Index-everything or untuned production changes | never |
| Trigram or full-text search for substring name matching | out of scope; noted in `lab38-sargability.md` as a deliberate choice with its own cost |

## Known discrepancy to resolve in the lab

The Lab 38 starter scripts reference `customer.email_normalized`, from the GUIDE's extended
track. The Lab 37 schema actually built uses `email`. Reconcile before running `03_indexes.sql`,
and do not rename a column that Lab 39 will map.

## Scope statement

Pre-lab only — prepare for lab; do not complete full Lab 38 now.

## Self mark

Overall prep: Pass
If Fail, revisit exercise(s): n/a
