# Lab 38 — Performance concepts

## The email lookup access path

One query, four possible paths, and the plan tells you which one you got.

| Path | When PostgreSQL picks it | Buffers here |
| --- | --- | --- |
| `Seq Scan` | no usable index, or the predicate matches most rows | 782 |
| `Index Scan` | selective predicate, index matches, heap fetch per row | 4 |
| `Index Only Scan` | as above and every column needed is in the index | 266 for the 556-row range query |
| `Bitmap Heap Scan` | mid-selectivity: too many rows for repeated random heap access, too few for a full scan | not reached in this lab |

The email case is the cleanest possible demonstration: one row out of 50,002. Without an index
PostgreSQL reads all 782 pages of the table and discards 50,001 rows. With
`ux_customer_email` it descends three or four index pages and fetches one heap page. The answer is
identical; the work is not.

Three things make an index unusable even when it exists, all of them visible in the plan:

1. **The column is wrapped.** `LOWER(email) = …` cannot use an index on `email`, because the index
   stores the column, not the expression. Plan shows `Seq Scan` with a `Filter`.
2. **The statistics are stale.** After a bulk load the planner may still believe the table is
   empty. Run `ANALYZE`. In this lab, autoanalyze had already fired, but the scripts run `ANALYZE`
   explicitly so a baseline is never a guess about timing.
3. **The predicate is not selective.** An index on `status` when 70% of rows are ACTIVE is
   correctly ignored: reading the index and then most of the heap is slower than reading the heap
   in order.

## Cardinality pitfalls

Cardinality is the planner's estimate of how many rows a node will produce. Every join order and
every scan choice follows from it, so a bad estimate produces a bad plan no matter how good the
indexes are.

**Read `rows=` against `actual rows`.** In lab38-004 the estimate was 34,916 and the actual 35,001,
about 0.2% off, which is why the plan was right. An order-of-magnitude gap is the signal to run
`ANALYZE` before believing anything else in the plan.

**Watch `loops=`.** Reported times are per loop. A node showing 0.003 ms with `loops=5020` cost
15 ms, not 0.003 ms. This is the single most misread number in `EXPLAIN ANALYZE` output.

**`Rows Removed by Filter` is wasted work made visible.** lab38-009 removed 49,446 rows to return
556. That number, not the elapsed time, is what says the predicate belongs in the index.

**Buffers beat milliseconds.** Wall-clock time moves with cache state and unrelated load on the
machine. Buffer counts are deterministic for a given plan and dataset, which is why every
comparison in `report.md` leads with buffers. The clearest example is lab38-006: adding an index
made the deep-OFFSET query four times faster in time while making it six times heavier in buffers.
Timing alone would have called that a win.

**Estimates degrade with correlation.** The planner assumes columns are independent. A filter on
`status = 'ACTIVE'` and `created_at` in the last week is estimated as the product of two
selectivities, which understates the count when new customers are mostly ACTIVE. Extended
statistics (`CREATE STATISTICS`) fix that case; it is out of scope here, but the underestimate is
worth recognising when a nested loop is chosen for what turns out to be a large result.

## Why the browser never sees any of this

The tuning here serves the Spring API from Labs 29 to 36, which is the only thing that talks to
PostgreSQL. The React SPA holds no connection and no credential. Ad-hoc SQL never reaches the
database from a client: the application sends parameterised statements, which is also what makes
plans comparable, since the same query text with the same bind shapes reuses the same plan.
