# Lab 38 — PostgreSQL SQL and CRM Query Performance

Module 38 · Checkpoint **E** · copied from the course starter into `examples/lab38-crm`,
then completed. Same file set as the starter, nothing added.

Evidence-based tuning: measure first, index second, measure again, and drop anything that cannot
show a regression when removed.

## Runbook

### 1. Use the Lab 37 container

The starter has no `compose.yaml` because this lab reuses the database from Lab 37.

```bash
cd ~/java-bootcamp/examples/lab37-crm
docker compose up -d
set -a; source .env; set +a
pgapp() { docker compose exec -T postgres psql -U crm_app -d "$POSTGRES_DB" "$@"; }
```

Everything in this lab runs in schema `crm_perf`, created by `database/ddl/02_schema.sql`. The
Lab 37 fixtures in `crm_app` are left untouched, so that lab's evidence stays reproducible and
this one can be dropped and re-run at will.

### 2. Apply the scripts in order

```bash
cd ~/java-bootcamp/examples/lab38-crm

# one-time, as the database owner: hand crm_app its own schema.
# crm_app has no CREATE on the database by design, so it cannot make one itself.
docker exec -i crm-postgres psql -U crm -d crm \
  -c "CREATE SCHEMA IF NOT EXISTS crm_perf AUTHORIZATION crm_app;"

pgapp < database/ddl/02_schema.sql              # tables, no performance indexes yet
pgapp < database/performance/01_generate_data.sql
pgapp < database/performance/02_baseline.sql   # ANALYZE, distribution, BEFORE plans
pgapp < database/performance/03_indexes.sql    # create indexes, AFTER plans
pgapp < database/performance/04_optimized.sql  # sargability, joins, paging
pgapp < database/performance/05_cleanup_indexes.sql  # challenge cycle, write cost
```

Order matters more here than in any previous lab. `02_baseline.sql` must run before
`03_indexes.sql`, because a baseline captured after the index is not a baseline.

### 3. Reset

```sql
DROP SCHEMA crm_perf CASCADE;
```

## Script purpose

| Script | Purpose |
| --- | --- |
| `database/ddl/02_schema.sql` | Lab 37 tables in schema `crm_perf`, deliberately without the email index |
| `01_generate_data.sql` | 50,000 synthetic customers, 70/30 skew, fixtures preserved, 5,001 accounts |
| `02_baseline.sql` | `ANALYZE`, distribution, documented binds, four BEFORE plans |
| `03_indexes.sql` | three indexes, `ANALYZE`, four AFTER plans, index sizes |
| `04_optimized.sql` | `date_trunc` versus half-open range with an equivalence proof, selective versus broad join, deterministic OFFSET, keyset paging at page 2 and page 251 |
| `05_cleanup_indexes.sql` | drop, re-measure, recreate for each index, plus insert write cost |

## Headline results

Full numbers in `database/performance/report.md`. Measured on PostgreSQL 16 with 50,002 customers.

| Query | Before | After | Change |
| --- | --- | --- | --- |
| Email lookup | Seq Scan, 782 buffers, 4.298 ms | Index Scan, 4 buffers, 0.095 ms | 196x fewer buffers |
| ACTIVE list page 1 | Seq Scan + Sort, 782 buffers, 10.947 ms | Index Scan, 23 buffers, 0.080 ms | no Sort node at all |
| Selective join | Hash Join, 50 buffers, 0.767 ms | Nested Loop, 6 buffers, 0.045 ms | 8x fewer buffers |
| Date filter | `date_trunc`, 782 buffers, 25.590 ms | half-open range, Index Only Scan, 266 buffers, 3.535 ms | identical 556 rows |
| Deep page 251 | OFFSET 5000, 5,047 buffers, 3.621 ms | keyset, 23 buffers, 0.048 ms | constant cost per page |

The result worth staring at is deep OFFSET. Adding the index made it four times faster in elapsed
time while making it six times heavier in buffers. Recording only milliseconds would have called
that a win and shipped it.

## Checkpoints

### Checkpoint A — Tooling and volume

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | `lab38-crm` under `examples/` | Pass |
| 2 | 50k customers with documented skew | Pass — 50,002 rows, ACTIVE 70.0% / PROSPECT 30.0%, `CUS-1001` and `CUS-1002` preserved |
| 3 | Statistics gathered and recorded | Pass — `ANALYZE` plus `pg_stat_user_tables` row counts and timestamps. PostgreSQL, so `ANALYZE`, not `DBMS_STATS` |

### Checkpoint B — Plans and indexes

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | Baseline email plan with actual rows and buffers | Pass — `EXPLAIN (ANALYZE, BUFFERS)`, the PostgreSQL equivalent of `ALLSTATS LAST` |
| 2 | Unique email index with improved plan evidence | Pass — 782 buffers to 4 |
| 3 | Status and created list index, measured | Pass — Sort node eliminated |

### Checkpoint C — Sargability, joins, paging

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | Half-open range versus `date_trunc` | Pass — identical 556-row id sets proved by symmetric `EXCEPT`, 7x faster |
| 2 | Selective versus broad join notes | Pass — nested loop and hash join, both correct, cardinality explains each |
| 3 | Deterministic OFFSET and keyset without duplicates or gaps | Pass — adjacent pages share 0 ids, three keyset pages give 60 distinct ids from 60 rows |

### Checkpoint D — Hygiene and report

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | Each retained index challenged | Pass — all three dropped, re-measured, recreated |
| 2 | `report.md` complete | Pass — plan, buffers, median time and write cost for 14 experiments |
| 3 | No secrets or dumps in Git | Pass — no passwords in any script, no row exports, fictional `example.test` emails only |

## Failure experiments

| # | Experiment | Observed | Restore |
| - | ---------- | -------- | ------- |
| 1 | `date_trunc(created_at)` versus half-open range | Seq Scan 25.590 ms versus Index Only Scan 3.535 ms, same 556 rows | prefer the range form |
| 2 | Drop `ux_customer_email`, re-run | back to Seq Scan, 782 buffers, 4.318 ms | recreate the unique index |
| 3 | Drop `ix_customer_status_created`, re-run | Seq Scan plus Sort returns, 10.824 ms | recreate |
| 4 | Drop `ix_account_customer`, re-run | Nested Loop degrades to Hash Join, 6 buffers to 50 | recreate |
| 5 | Deep `OFFSET 5000` versus keyset | 5,047 buffers versus 23 | use keyset for deep pages |
| 6 | Insert 1,000 rows with all indexes | 14.617 ms, 11,376 buffers, 44 pages dirtied | this is the price of the reads above |

## Security and production review

* **Untrusted inputs:** application binds only. No ad-hoc SQL reaches production, and the browser
  never touches the database.
* **Enforcement:** constraints from Lab 37 still hold; authorization stays in Spring.
* **Sensitive values:** no passwords in any script, no production dumps, no real emails. Synthetic
  addresses use `example.test`.

## Reflection

1. **Which design decision most affected correctness?** Capturing every baseline before creating
   any index, and comparing buffers rather than milliseconds. That is what exposed the deep-OFFSET
   result, where the index made the query faster and heavier at the same time.
2. **What evidence proves it works?** Fourteen numbered experiments in `report.md`, each with plan,
   buffers and time, plus two correctness proofs: identical id sets for the sargable rewrite, and
   60 distinct ids across three keyset pages.
3. **Which failure was hardest to diagnose?** The first keyset measurement computed its own anchor
   with `OFFSET 4999` inside the query, so the plan measured the anchor lookup rather than the page
   seek and reported 5,075 buffers. Binding the anchor as a parameter, which is what a real page-2
   request does, revealed the true cost of 23 buffers.
