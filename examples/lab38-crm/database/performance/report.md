# Lab 38 — Performance report

All numbers below were measured, not estimated: PostgreSQL 16, schema `crm_perf`, 50,002
customers and 5,001 accounts, `ANALYZE` run before every baseline, each statement executed twice
with the second run recorded.

## Dataset and binds

| Item | Value |
| --- | --- |
| Customers | 50,002 (50,000 synthetic + `CUS-1001` + `CUS-1002`) |
| Status skew | ACTIVE 35,001 (70.0%), PROSPECT 15,001 (30.0%) |
| Accounts | 5,001 (every 10th bulk customer, plus Amina) |
| `created_at` spread | last 90 days |
| email bind | `user000001@example.test` |
| public_id bind | `CUS-1001` |
| status bind | `ACTIVE` |
| page size | 20 |
| deep offset | 5000 |

A note on "plan hash": PostgreSQL has no Oracle-style `PLAN_HASH_VALUE`. The stable identity of a
plan here is its top-level node plus estimated cost, which is what the Plan column records. With
`pg_stat_statements` enabled, `queryid` serves the same purpose across sessions.

## Experiments

| Experiment | Plan | Buffers | Median time | Verdict |
| ---------- | ---- | ------- | ----------- | ------- |
| lab38-001 baseline email lookup | `Seq Scan on customer`, cost 1407.03, 50,001 rows removed by filter | 782 | 4.298 ms | one row found by reading the entire table |
| lab38-002 after `ux_customer_email` | `Index Scan using ux_customer_email`, cost 8.43 | 4 | 0.095 ms | **196x fewer buffers, 45x faster** |
| lab38-003 baseline ACTIVE list page 1 | `Seq Scan` + `Sort` (top-N heapsort, 26 kB) | 782 | 10.947 ms | reads 50k rows and sorts 35k to return 20 |
| lab38-004 after `ix_customer_status_created` | `Index Scan using ix_customer_status_created`, no Sort node | 23 | 0.080 ms | **34x fewer buffers, 137x faster** |
| lab38-005 deep OFFSET 5000, no index | `Seq Scan` + `Sort` (982 kB) | 782 | 16.607 ms | sorts 5,020 rows to discard 5,000 |
| lab38-006 deep OFFSET 5000, with index | `Index Scan`, 5,020 rows read | 5,047 | 3.621 ms | faster, but buffers grew 219x versus page 1: the index does not fix OFFSET |
| lab38-007 baseline selective join | `Hash Join`, full scan of `account` | 50 | 0.767 ms | hashes all 5,001 accounts to match one |
| lab38-008 after `ix_account_customer` | `Nested Loop` + two index scans | 6 | 0.045 ms | **8x fewer buffers, 17x faster** |
| lab38-009 `date_trunc(created_at)` | `Seq Scan`, 49,446 rows removed by filter | 782 | 25.590 ms | non-sargable, the column is wrapped |
| lab38-010 half-open range | `Index Only Scan`, `Heap Fetches: 0` | 266 | 3.535 ms | **7x faster, identical 556 rows** |
| lab38-011 join, selective (one customer) | `Nested Loop` | 6 | 0.095 ms | correct shape for one driving row |
| lab38-012 join, broad (all ACTIVE) | `Hash Join`, 5,001 rows out | 829 | 10.420 ms | correct shape for a report; nested loop here would be 35k probes |
| lab38-013 keyset page 2 | `Index Scan`, `Index Cond` includes the row-value comparison | 23 | 0.056 ms | seeks straight to position |
| lab38-014 keyset at depth 5000 | same plan, same shape | 23 | 0.048 ms | **constant cost: page 251 costs what page 2 costs** |

## Why keyset beats deep OFFSET

`OFFSET 5000` does not skip work, it does the work and throws it away. lab38-006 shows the engine
reading 5,020 rows through the index and discarding 5,000 of them: 5,047 buffers for 20 rows.
Adding the index made that query faster in wall-clock terms while making it *heavier* in buffers
than the sequential scan, which is exactly the kind of result that gets missed when only elapsed
time is recorded.

Keyset paging turns the position into a predicate. The row-value comparison
`(created_at, customer_id) < ($ts, $id)` becomes part of the `Index Cond`, so the engine seeks
directly to the entry and reads 20 rows:

```text
Index Cond: (((status)::text = 'ACTIVE'::text)
             AND (ROW(created_at, customer_id) < ROW('2026-08-06 20:41:57+00'::timestamptz, '5681'::bigint)))
Buffers: shared hit=23
```

23 buffers at depth 5000, versus 5,047 for the equivalent OFFSET page. Page 2 and page 251 have
identical cost, which is the property that makes keyset safe for infinite scrolling.

The tie-breaker is not optional. Sorting on `created_at` alone is non-deterministic when rows
share a timestamp, and a non-deterministic sort makes keyset skip or repeat rows. Both correctness
checks passed: adjacent OFFSET pages shared 0 ids, and a three-page keyset walk returned 60
distinct ids from 60 rows.

## Sargability

`date_trunc('day', created_at) = …` and the half-open range return **identical** id sets, verified
by symmetric `EXCEPT`: 556 rows each, 0 rows in one and not the other. The plans are not identical
at all:

| Form | Plan | Buffers | Time |
| --- | --- | --- | --- |
| `date_trunc('day', created_at) = X` | Seq Scan, 49,446 rows removed by filter | 782 | 25.590 ms |
| `created_at >= X AND created_at < X + 1 day` | Index Only Scan, Heap Fetches 0 | 266 | 3.535 ms |

Wrapping the column hides it from the index. The rewrite is free: same answer, one seventh of the
time, and `Heap Fetches: 0` means the answer came entirely from the index without touching the
table at all.

## Join strategies

Both plans are correct; the planner picked each for the right reason.

* **Selective** (`WHERE public_id = 'CUS-1001'`): `Nested Loop`, 6 buffers. One driving row, one
  index probe into `account`. A hash join here would build a 5,001-row hash table to match one row.
* **Broad** (`WHERE status = 'ACTIVE'`): `Hash Join`, 829 buffers for 5,001 result rows. With 35,001
  driving rows, a nested loop would mean 35,001 index probes; hashing `account` once is cheaper.

"Nested loop good, hash join bad" is not a rule. Cardinality decides, which is why `ANALYZE`
matters as much as the indexes do.

## Index challenge cycle

Each index was dropped, its query re-measured, then recreated.

| Index | Without it | With it | Retained |
| --- | --- | --- | --- |
| `ux_customer_email` | Seq Scan, 782 buffers, 4.318 ms | Index Scan, 4 buffers, 0.095 ms | **yes** |
| `ix_customer_status_created` | Seq Scan + Sort, 788 buffers, 10.824 ms | Index Scan, 23 buffers, 0.080 ms | **yes** |
| `ix_account_customer` | Hash Join, 50 buffers, 0.767 ms | Nested Loop, 6 buffers, 0.045 ms | **yes** |

All three survive the challenge with a measurable regression when removed. Nothing speculative was
kept: no index on `status` alone (70% of rows are ACTIVE, so the planner would ignore it), no
index on `full_name`, no duplicate of the existing unique constraints.

## Write cost

| Measure | Value |
| --- | --- |
| Insert 1,000 rows with all indexes present | 14.617 ms, 11,376 buffers, 44 pages dirtied |
| `ux_customer_email` size | 2,064 kB |
| `ix_customer_status_created` size | 2,208 kB |
| `ix_account_customer` size | 128 kB |
| Total added index storage | ~4.4 MB against a ~6 MB table |

Roughly 14.6 µs per inserted row, and the three retained indexes cost about 4.4 MB of storage plus
maintenance on every write. That is the price of the read improvements above. It is worth paying
here because each index has a measured query behind it; the same 4.4 MB spent on speculative
indexes would buy write amplification and nothing else.

## Reproduce

```bash
psql ... -f database/ddl/02_schema.sql
psql ... -f database/performance/01_generate_data.sql
psql ... -f database/performance/02_baseline.sql       # BEFORE plans
psql ... -f database/performance/03_indexes.sql        # AFTER plans
psql ... -f database/performance/04_optimized.sql      # sargability, joins, paging
psql ... -f database/performance/05_cleanup_indexes.sql # challenge cycle + write cost
```

Absolute timings vary by machine; buffer counts do not. Compare buffers first.
