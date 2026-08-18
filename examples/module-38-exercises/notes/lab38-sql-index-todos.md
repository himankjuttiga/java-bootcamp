# Lab 38 — Fill SQL/Index TODOs

## Step 1 — Paste

The full draft lives beside this file as `notes/lab38-todos.sql`. The shape of it:

```sql
-- baseline (avoid)
SELECT * FROM customer
WHERE lower(full_name) = 'amina khan';

-- optimized lookup
SELECT customer_id, full_name, status
FROM customer
WHERE public_id = 'CUS-1001';

-- supporting index
CREATE INDEX ix_customer_status_created ON customer (status, created_at DESC, customer_id DESC);

-- paging sketch
SELECT customer_id, full_name
FROM customer
ORDER BY customer_id
LIMIT 20 OFFSET 0;
```

## Step 2 — Fill

| Blank | Value | Why |
| --- | --- | --- |
| wrapping function | `lower` | shows the non-sargable case the baseline is meant to expose |
| lookup key | `'CUS-1001'` on `public_id` | the SPA and API use the business id, not the surrogate |
| status index name | `ix_customer_status_created` | composite, not `idx_customer_status`: status alone is too low-selectivity to help |
| account index name | `ix_account_customer` | already created in Lab 37, so not recreated here |
| page size / offset | `20`, `0` | fine at page 1, which is exactly why the deep-page experiment matters |

Two deliberate departures from the exercise's suggested answers. The template suggests
`idx_customer_status` on `status` alone; a single-column index on a low-selectivity column is the
index the lab is supposed to teach you to reject, so the draft uses the composite that serves the
real list query. And `idx_account_customer` is not created, because Lab 37 already created
`ix_account_customer`; adding a second index on the same column would double the write cost for
nothing.

Naming follows the Lab 37 convention, `ix_` for a plain index, matching `pk_`, `uk_`, `fk_` and
`ck_` there.

## Step 3 — Keyset note

```sql
-- TODO: prefer keyset pagination (WHERE customer_id > :last) for deep pages
```

Implemented in the draft as a row-value comparison, which is the form that stays correct when the
sort has a tie-breaker:

```sql
WHERE (created_at, customer_id) < (:last_created_at, :last_customer_id)
ORDER BY created_at DESC, customer_id DESC
LIMIT 50
```

`OFFSET 100000` makes the database build and discard 100000 rows before returning 50. Keyset
seeks directly to the position in the index, so page 5000 costs what page 1 costs. That is the
answer to "what goes in `report.md` for lab38-003 versus lab38-004": the same rows, wildly
different buffer counts.

## Step 4 — No run

Nothing here has been executed. Lab 38 generates 50k rows, runs `ANALYZE`, then measures with
`EXPLAIN (ANALYZE, BUFFERS)` before and after each index, recording both plans in
`database/performance/report.md` under the experiment ids `lab38-001` to `lab38-004`.

### Column-name warning for the lab

The Lab 38 starter scripts refer to `customer.email_normalized`, which belongs to the GUIDE's
extended track. The Lab 37 timed-path schema in `examples/lab37-crm` uses `email`. Pick the
schema that actually exists before running `03_indexes.sql`, and do not silently rename a column
Lab 39 will map through JPA.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
