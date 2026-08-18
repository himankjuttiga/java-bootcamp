# Lab 38 — EXPLAIN Checklist

## Step 1 — Command

```sql
EXPLAIN (ANALYZE, BUFFERS) <sql>;
```

`EXPLAIN` alone prints the planner's guess. `ANALYZE` actually runs the statement and reports
what happened, which is the only version worth recording. `BUFFERS` adds the page counts, which
is the measurement that does not move when the laptop is busy.

Two cautions: `EXPLAIN ANALYZE` on an `INSERT`, `UPDATE` or `DELETE` really performs it, so wrap
those in `BEGIN; ... ROLLBACK;`. And run each query twice, recording the second, because the
first pays for a cold cache.

## Step 2 — Look for

| Line | Meaning | When it is fine |
| --- | --- | --- |
| `Seq Scan` | reads every page of the table | small tables, or when returning most rows |
| `Index Scan` | walks the index, then fetches each matching heap row | selective predicates |
| `Index Only Scan` | answers entirely from the index, no heap fetch | all needed columns are in the index and the visibility map is current |
| `Bitmap Heap Scan` | collects matches in a bitmap, then reads the heap in physical order | mid-selectivity, many scattered rows |
| `Nested Loop` | for each outer row, probe the inner | small outer side with an indexed inner |
| `Hash Join` | builds a hash of one side | large unsorted joins |
| `Sort` with `external merge Disk` | the sort spilled to disk | never fine, raise `work_mem` or index the sort |
| `Filter` with a large `Rows Removed by Filter` | rows read then thrown away | a sign the predicate is not being served by the index |

Numbers to read, in order of usefulness:

1. **`rows=` estimated versus `actual rows`**. An order-of-magnitude gap means the statistics are
   wrong and every plan choice downstream is guesswork.
2. **`Buffers: shared hit=… read=…`**. `hit` came from cache, `read` came from disk. This is the
   stable metric to compare before and after; wall-clock time moves with unrelated load.
3. **`loops=`**. Reported times are per loop. A node showing 0.05 ms with `loops=50000` cost
   two and a half seconds, not 0.05 ms.
4. **`actual time`**. Useful, but take the median of a few runs.

## Step 3 — Success signal

For the Amina lookup, the good plan is:

```text
Index Scan using uk_customer_public on customer  (cost=0.29..8.31 rows=1 width=…)
  Index Cond: ((public_id)::text = 'CUS-1001'::text)
  Buffers: shared hit=3
```

Three signals in one: the index is named, so the right one was chosen; `Index Cond` rather than
`Filter`, so the predicate was pushed into the index rather than applied after reading; and a
handful of buffers instead of hundreds.

The failure to watch for on the list query is `Sort` appearing above the scan. If the composite
index really covers filter and order, the rows arrive sorted and no sort node is needed.

## Step 4 — Analyze

```sql
ANALYZE customer;
ANALYZE;                  -- whole database
```

`ANALYZE` refreshes the planner's statistics: row counts, most common values, histograms. This is
PostgreSQL, not Oracle: there is no `DBMS_STATS.GATHER_TABLE_STATS`, and any course material
saying otherwise is inherited from the Oracle version of this course.

Stale statistics are the answer to the predict question. A `Seq Scan` on an email equality with a
unique index present usually means one of:

* the table was just bulk-loaded and autovacuum has not analysed it yet, so run `ANALYZE`
* a type mismatch, for example comparing a `varchar` column to a parameter the planner treats as
  a different type, so the index cannot be used
* the predicate wraps the column in a function, `LOWER(email) = …`, which is not sargable
* the table is genuinely tiny, where a sequential scan is cheaper and the planner is right

Autovacuum runs `ANALYZE` automatically after enough changes, but after a 50k-row bulk insert it
may not have caught up, so run it explicitly before recording a baseline.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
