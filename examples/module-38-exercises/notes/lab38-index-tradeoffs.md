# Lab 38 — Index Tradeoffs

## Step 1 — Benefit

| Index | Serves | Expected effect |
| --- | --- | --- |
| `uk_customer_email` (already exists) | email equality on login | index scan returning one row instead of a full scan of 50k |
| `uk_customer_public` (already exists) | `CUS-1001` lookups | same, and it doubles as the uniqueness guarantee |
| `ix_customer_status_created` on `(status, created_at, customer_id)` | the list view: filter by status, sort by created_at | filter and sort satisfied by one structure, no separate sort step |
| `ix_account_customer` (already exists) | accounts for a customer, and parent deletes | avoids scanning `account` on every join and every FK check |

The composite column order is not arbitrary. Equality predicates come first, then the range or
sort columns: `(status, created_at, customer_id)` lets the planner seek straight to the `ACTIVE`
section and read it already in `created_at` order. Reversing it to `(created_at, status)` would
force a scan of the whole date range with a filter applied afterwards.

## Step 2 — Cost

Every index is a second structure that must be kept in step with the table.

| Cost | Detail |
| --- | --- |
| Write amplification | each `INSERT` writes one row plus one entry per index. Seeding 50k customers with four indexes is five structures maintained, not one |
| Update cost | an `UPDATE` touching an indexed column rewrites that index entry. PostgreSQL can skip index maintenance entirely for a HOT update where no indexed column changed, and every extra index makes that path less likely |
| Storage | a B-tree on a text column can approach the size of the column data itself, and it competes for the same shared buffer cache the table wants |
| Vacuum | dead index entries have to be cleaned up too, so autovacuum works harder |
| Planner risk | more candidate paths means more chances to pick a bad one on stale statistics |

Answering the predict question directly: a unique index on email does **not** help inserts, it
slows them, because every insert must probe the index to prove no duplicate exists. It is still
worth having, because that probe is the uniqueness guarantee itself. A constraint that is also a
fast lookup is the one case where the write cost buys two things at once.

## Step 3 — Cleanup

`05_cleanup_indexes.sql` exists to challenge each index rather than to tidy up. The discipline is:

1. Record the plan and timing with the index present.
2. `DROP INDEX`, re-run `EXPLAIN (ANALYZE, BUFFERS)`, record again.
3. Recreate it, and keep the before and after in `report.md`.

If the plan and the timing barely move, the index is not earning its write cost and should not
survive the lab. An index nobody can produce evidence for is a liability that looks like caution.

Use `DROP INDEX CONCURRENTLY` and `CREATE INDEX CONCURRENTLY` when the table is live: the plain
forms take locks that block writers for the duration.

## Step 4 — Rule

**Add an index only when a plan shows the need, and keep it only when a second plan shows the
gain.**

The low-selectivity trap from the debug question: an index on `status` alone is useless when most
rows share a value. If 80 percent of customers are `ACTIVE`, reading the index and then fetching
80 percent of the heap pages is slower than scanning the table directly, and the planner will
correctly refuse to use it. Two ways to make it useful:

* Make it composite, `(status, created_at, customer_id)`, so it serves the sort as well.
* Make it partial, `CREATE INDEX ... ON customer (created_at) WHERE status = 'ACTIVE'`, which
  indexes only the rows the query wants and stays small.

Selectivity is the whole question. A rough rule: below roughly 5 to 10 percent of the table an
index scan usually wins; above that a sequential scan usually does. The planner decides this per
query from statistics, which is why `ANALYZE` matters as much as the index does.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
