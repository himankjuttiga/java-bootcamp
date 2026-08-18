# Lab 38 — Access Patterns

## Step 1 — Patterns

The queries the CRM actually runs, taken from the Lab 33 to 36 SPA and the Lab 29 API:

| # | Pattern | SQL shape | Where it comes from |
| - | ------- | --------- | ------------------- |
| 1 | Lookup one customer by business id | `WHERE public_id = 'CUS-1001'` | `GET /api/customers/{id}`, the SPA detail view |
| 2 | Lookup by email | `WHERE email = 'amina@example.com'` | login, duplicate check on create |
| 3 | List by status, newest first | `WHERE status = 'ACTIVE' ORDER BY created_at DESC, customer_id DESC LIMIT 50` | the SPA list view |
| 4 | Created-date range | `WHERE created_at >= $from AND created_at < $to` | reporting, "new this month" |
| 5 | Accounts for one customer | `JOIN account a ON a.customer_id = c.customer_id` | the customer detail panel |
| 6 | Paging through the list | `ORDER BY ... LIMIT 50 OFFSET n` | the SPA pagination |

## Step 2 — Hot path

Patterns 1 and 2 are the hottest, and they are hot for different reasons.

Pattern 1 runs on every detail view and every write, so it is the highest volume. It is already
covered: `uk_customer_public` creates an index, so the lookup is an index scan on a unique key
returning at most one row. There is nothing to tune, which is the correct outcome of designing
the keys properly in Lab 37.

Pattern 2 is the login path. Unique index, single row, same story. The predict question asks
which is hottest for login and email lookup: it is the email equality, and `uk_customer_email`
already serves it, so the interesting question in the lab is whether the plan actually uses it.
An index that exists but goes unused is the thing EXPLAIN is for.

Pattern 3 is where the tuning work lives. `status` alone is low selectivity: if most customers
are `ACTIVE`, an index on status returns most of the table and the planner will correctly ignore
it in favour of a sequential scan. The composite `(status, created_at, customer_id)` is what
makes the list query fast, because it satisfies the filter and the sort in one structure.

## Step 3 — Anti-pattern

| Anti-pattern | Why it hurts | What to do instead |
| --- | --- | --- |
| `SELECT *` with no `WHERE` on a large table | reads every page, ships columns nobody uses | select named columns, always bound the result |
| Deep `OFFSET 100000` | the database still produces and discards every skipped row | keyset pagination, see `lab38-sargability.md` |
| Indexing every column "just in case" | every index is rewritten on insert and update, and the planner has more paths to get wrong | add an index only when a plan proves the need |
| N+1: one query per customer to fetch accounts | 51 round trips for a 50-row page | one join, or one `IN` query |
| Counting with `SELECT count(*)` on every page load | full scan on a big table for a number nobody reads carefully | estimate from `pg_class.reltuples`, or drop the total |

Indexing every column is the specific write-cost trap the debug question asks about. Each
additional index means another structure to maintain on every `INSERT` and on any `UPDATE` that
touches an indexed column. PostgreSQL can skip index maintenance entirely for an update where no
indexed column changed, a HOT update, and every extra index makes that optimisation less likely.

## Step 4 — Notes

Saved in `notes/lab38-perf.md`. Measurements happen in the lab against a table with 50k rows;
nothing here was executed.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
