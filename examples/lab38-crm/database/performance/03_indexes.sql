-- Lab 38 — indexes for the queries measured in 02_baseline.sql, then the AFTER plans.
-- Every index here answers a plan that was captured first. Nothing speculative.

\set ON_ERROR_STOP on
SET search_path = crm_perf;

\echo '=== CREATE INDEXES ==='

-- lab38-002: email equality is the login path, one row out of 50k. Unique, because the column
-- is also a business uniqueness rule: the write cost buys the constraint and the lookup at once.
CREATE UNIQUE INDEX IF NOT EXISTS ux_customer_email ON customer (email);

-- lab38-004: the list page filters on status and sorts by (created_at, customer_id).
-- Equality column first, then the sort columns in the order and direction the query asks for,
-- so the index satisfies the filter AND the ordering with no separate Sort node.
-- status alone would be useless here: ~70% of rows are ACTIVE.
CREATE INDEX IF NOT EXISTS ix_customer_status_created
  ON customer (status, created_at DESC, customer_id DESC);

-- lab38-008: PostgreSQL never indexes the referencing side of a foreign key for you.
CREATE INDEX IF NOT EXISTS ix_account_customer ON account (customer_id);

ANALYZE customer;
ANALYZE account;

\echo '=== lab38-002 AFTER: email lookup with ux_customer_email ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, full_name, status
FROM customer
WHERE email = 'user000001@example.test';

\echo '=== lab38-004 AFTER: ACTIVE list page 1 with ix_customer_status_created ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, created_at
FROM customer
WHERE status = 'ACTIVE'
ORDER BY created_at DESC, customer_id DESC
LIMIT 20;

\echo '=== lab38-006 AFTER: deep OFFSET 5000 with the index (still pays for skipped rows) ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, created_at
FROM customer
WHERE status = 'ACTIVE'
ORDER BY created_at DESC, customer_id DESC
LIMIT 20 OFFSET 5000;

\echo '=== lab38-008 AFTER: selective join with ix_account_customer ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.public_id, a.account_number, a.balance_cents
FROM customer c
JOIN account a ON a.customer_id = c.customer_id
WHERE c.public_id = 'CUS-1001';

\echo '=== INDEX SIZES: the storage half of the write cost ==='
SELECT indexrelname AS index_name,
       pg_size_pretty(pg_relation_size(indexrelid)) AS size,
       idx_scan AS scans_since_creation
FROM pg_stat_user_indexes
WHERE schemaname = 'crm_perf'
ORDER BY pg_relation_size(indexrelid) DESC;
