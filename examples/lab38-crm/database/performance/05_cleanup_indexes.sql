-- Lab 38 — index challenge cycle.
--
-- Every index is dropped, the query it exists for is re-measured, and the index is recreated.
-- An index that survives this without a visible regression has not earned its write cost.

\set ON_ERROR_STOP on
SET search_path = crm_perf;

\echo '=== CHALLENGE 1: drop ux_customer_email, re-measure the email lookup ==='
DROP INDEX IF EXISTS ux_customer_email;
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, full_name, status
FROM customer
WHERE email = 'user000001@example.test';

CREATE UNIQUE INDEX ux_customer_email ON customer (email);

\echo '=== CHALLENGE 2: drop ix_customer_status_created, re-measure the ACTIVE list ==='
DROP INDEX IF EXISTS ix_customer_status_created;
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, created_at
FROM customer
WHERE status = 'ACTIVE'
ORDER BY created_at DESC, customer_id DESC
LIMIT 20;

CREATE INDEX ix_customer_status_created
  ON customer (status, created_at DESC, customer_id DESC);

\echo '=== CHALLENGE 3: drop ix_account_customer, re-measure the selective join ==='
DROP INDEX IF EXISTS ix_account_customer;
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.public_id, a.account_number, a.balance_cents
FROM customer c
JOIN account a ON a.customer_id = c.customer_id
WHERE c.public_id = 'CUS-1001';

CREATE INDEX ix_account_customer ON account (customer_id);

ANALYZE customer;
ANALYZE account;

\echo '=== WRITE COST: the price of the retained indexes ==='
-- Insert 1000 rows, then delete them, with all indexes present. Compare the timing to the
-- same operation before indexes existed (recorded during 01_generate_data.sql).
BEGIN;
EXPLAIN (ANALYZE, BUFFERS)
INSERT INTO customer (public_id, full_name, email, status)
SELECT 'CUS-WCOST-' || i, 'Write Cost ' || i, 'wcost' || i || '@example.test', 'ACTIVE'
FROM generate_series(1, 1000) AS g(i);
ROLLBACK;

\echo '=== RETAINED INDEXES AND SIZES ==='
SELECT indexrelname AS index_name,
       pg_size_pretty(pg_relation_size(indexrelid)) AS size,
       idx_scan AS scans
FROM pg_stat_user_indexes
WHERE schemaname = 'crm_perf'
ORDER BY indexrelname;
