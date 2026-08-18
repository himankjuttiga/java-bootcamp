-- Lab 38 — offline draft (pre-lab). Nothing here is executed until the graded lab measures it.
-- Column names follow the Lab 37 timed-path schema in examples/lab37-crm: `email`, not
-- `email_normalized`. Lab 39 maps these names, so they must not drift.

-- ---------------------------------------------------------------- baseline (avoid)
-- Non-sargable: the column is wrapped, so a plain index on full_name cannot be used.
SELECT * FROM customer
WHERE lower(full_name) = 'amina khan';

-- Deep OFFSET: the database produces and discards every skipped row.
SELECT customer_id, full_name
FROM customer
ORDER BY customer_id
LIMIT 20 OFFSET 0;          -- harmless at page 1, ruinous at OFFSET 100000

-- ---------------------------------------------------------------- optimized lookups
SELECT customer_id, full_name, status
FROM customer
WHERE public_id = 'CUS-1001';

SELECT customer_id, full_name, status
FROM customer
WHERE email = 'amina@example.com';

-- List page: filter and sort served by one composite index.
SELECT customer_id, public_id, full_name, created_at
FROM customer
WHERE status = 'ACTIVE'
ORDER BY created_at DESC, customer_id DESC
LIMIT 50;

-- ---------------------------------------------------------------- supporting indexes
-- uk_customer_public and uk_customer_email already index public_id and email; do not duplicate.
CREATE INDEX ix_customer_status_created ON customer (status, created_at DESC, customer_id DESC);

-- ix_account_customer already exists from Lab 37. Kept here as the reference for why:
-- CREATE INDEX ix_account_customer ON account (customer_id);

-- Alternative when ACTIVE is a small slice of the table:
-- CREATE INDEX ix_customer_active_created ON customer (created_at DESC)
--   WHERE status = 'ACTIVE';

-- Only if a plan proves LOWER(full_name) searches are real traffic:
-- CREATE INDEX ix_customer_lower_name ON customer (lower(full_name));

-- ---------------------------------------------------------------- paging
-- TODO: prefer keyset pagination (WHERE customer_id > :last) for deep pages
SELECT customer_id, public_id, full_name, created_at
FROM customer
WHERE status = 'ACTIVE'
  AND (created_at, customer_id) < (:last_created_at, :last_customer_id)
ORDER BY created_at DESC, customer_id DESC
LIMIT 50;

-- ---------------------------------------------------------------- date range
-- TODO: never wrap created_at in date_trunc for a filter; use a half-open range
SELECT count(*)
FROM customer
WHERE created_at >= :from_ts
  AND created_at <  :to_ts;

-- ---------------------------------------------------------------- lab procedure
-- TODO Lab 38: ANALYZE customer; before recording any baseline
-- TODO Lab 38: EXPLAIN (ANALYZE, BUFFERS) each query twice, record the second run
-- TODO Lab 38: drop each index, re-EXPLAIN, restore, and log both plans in report.md
