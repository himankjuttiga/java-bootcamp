# Lab 38 — Sargability

A predicate is sargable when the database can use it as a search argument against an index, that
is, when the indexed column appears bare on one side of the comparison. Wrap the column in
anything and the index on that column no longer matches.

## Reference

| Predicate | Sargable? | Why |
| --- | --- | --- |
| `public_id = 'CUS-1001'` | Yes | bare column, equality, unique index |
| `email = 'amina@example.com'` | Yes | same |
| `status = 'ACTIVE'` | Yes with an index, but low selectivity may make a scan cheaper anyway | |
| `LOWER(full_name) = 'amina khan'` | No, on a plain index | the index stores `full_name`, not `LOWER(full_name)` |
| `created_at >= TIMESTAMPTZ '2026-01-01'` | Yes | a range on a bare column |
| `date_trunc('day', created_at) = DATE '2026-01-01'` | No | the column is wrapped |
| `full_name LIKE 'Amina%'` | Yes for a prefix, with the right operator class | a leading wildcard is not |
| `full_name LIKE '%Khan'` | No | nothing to seek to |
| `balance_cents / 100 > 1000` | No | arithmetic on the column |
| `customer_id IN (1, 2, 3)` | Yes | equivalent to several equalities |
| `status <> 'CLOSED'` | Rarely useful | negation usually matches most rows |

## Step 1 — Study table

Copied above, extended with the cases the CRM actually produces.

## Step 2 — Rewrite the non-sargable name search

Three options, in increasing cost:

```sql
-- 1. Functional index: index the expression the query uses.
CREATE INDEX ix_customer_lower_name ON customer (LOWER(full_name));
-- now WHERE LOWER(full_name) = 'amina khan' is sargable against THAT index

-- 2. Store it normalised, and index the column.
--    This is exactly why the GUIDE's extended track has email_normalized.
ALTER TABLE customer ADD COLUMN name_normalized VARCHAR(200)
  GENERATED ALWAYS AS (LOWER(full_name)) STORED;
CREATE INDEX ix_customer_name_normalized ON customer (name_normalized);

-- 3. Case-insensitive prefix search with the right operator class.
CREATE INDEX ix_customer_name_pattern ON customer (LOWER(full_name) text_pattern_ops);
-- serves WHERE LOWER(full_name) LIKE 'amina%'
```

`ILIKE '%amina%'` cannot use any B-tree at all: with a leading wildcard there is nothing to seek
to. Substring search on a big table needs a trigram index (`pg_trgm`) or full text search, and
both are a deliberate choice with their own write cost, not a free fix.

## Step 3 — Half-open range

The rewrite the predict question asks for:

```sql
-- non-sargable: the column is wrapped
WHERE date_trunc('day', created_at) = CURRENT_DATE

-- sargable: a half-open range on the bare column
WHERE created_at >= CURRENT_DATE
  AND created_at <  CURRENT_DATE + INTERVAL '1 day'
```

Half-open, `>= start AND < end`, not `BETWEEN`. `BETWEEN` is inclusive at both ends, so a
timestamp landing exactly on midnight of the end date gets counted in two adjacent buckets. With
`TIMESTAMPTZ` this also avoids ever having to reason about what `<= '2026-01-31'` means at
23:59:59.999999.

Note that `CURRENT_DATE` compared against a `TIMESTAMPTZ` is evaluated in the session time zone.
For reporting that must agree across regions, pass explicit `TIMESTAMPTZ` bounds from the
application rather than relying on the session setting.

## Step 4 — Pagination is a sargability problem too

```sql
-- deep OFFSET: the database still produces and discards 100000 rows
SELECT ... ORDER BY created_at DESC, customer_id DESC LIMIT 50 OFFSET 100000;

-- keyset: seek straight to the position, constant cost per page
SELECT ... FROM customer
WHERE status = 'ACTIVE'
  AND (created_at, customer_id) < ($last_created_at, $last_customer_id)
ORDER BY created_at DESC, customer_id DESC
LIMIT 50;
```

The row-value comparison `(a, b) < ($1, $2)` is sargable against the composite index
`(status, created_at, customer_id)`, so page 5000 costs the same as page 1. Writing it as
`created_at < $1 OR (created_at = $1 AND customer_id < $2)` is logically the same but the planner
handles the tuple form better, and the tuple form cannot be got subtly wrong.

The tie-breaker column matters: sorting on `created_at` alone is not deterministic when two rows
share a timestamp, and a non-deterministic sort makes keyset paging skip or repeat rows.

## Step 5 — Oracle note

Older material in this course says `TRUNC(created_at) = TRUNC(SYSDATE)`. The PostgreSQL mapping:

| Oracle | PostgreSQL | Verdict |
| --- | --- | --- |
| `TRUNC(created_at)` | `date_trunc('day', created_at)` | both non-sargable on a plain index |
| `SYSDATE` | `CURRENT_DATE` / `now()` | |
| function-based index | functional index, `CREATE INDEX ... ON t (expr)` | both rescue the wrapped predicate |

The lesson survives the dialect change: rewrite to a range on the bare column first, and reach
for a functional index only when the expression is genuinely required.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
