-- Lab 38 — statistics, distribution, binds, and the BEFORE plans.
-- Run this before 03_indexes.sql. A baseline captured after the index is not a baseline.
--
-- PostgreSQL, not Oracle: ANALYZE replaces DBMS_STATS.GATHER_TABLE_STATS, and
-- EXPLAIN (ANALYZE, BUFFERS) replaces DBMS_XPLAN.DISPLAY_CURSOR(... 'ALLSTATS LAST').

\set ON_ERROR_STOP on
SET search_path = crm_perf;

\echo '=== STATS: plans after a bulk load without ANALYZE are fiction ==='
ANALYZE customer;
ANALYZE account;

SELECT relname AS table_name, n_live_tup AS approx_rows, last_analyze, last_autoanalyze
FROM pg_stat_user_tables
WHERE schemaname = 'crm_perf'
ORDER BY relname;

\echo '=== DISTRIBUTION AND BINDS (record these in report.md so a peer can re-run) ==='
SELECT status, count(*) AS cnt FROM customer GROUP BY status ORDER BY status;

-- Binds used by every measurement in this lab:
--   email bind      : user000001@example.test   (typical bulk row)
--   public_id bind  : CUS-1001                  (Amina, selective)
--   status bind     : ACTIVE                    (~70% of rows, low selectivity)
--   page size       : 20
--   deep offset     : 5000

\echo '=== lab38-001 BASELINE: email lookup, no index on email ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, full_name, status
FROM customer
WHERE email = 'user000001@example.test';

\echo '=== lab38-003 BASELINE: ACTIVE list page 1, no status index ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, created_at
FROM customer
WHERE status = 'ACTIVE'
ORDER BY created_at DESC, customer_id DESC
LIMIT 20;

\echo '=== lab38-005 BASELINE: deep OFFSET 5000 ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, created_at
FROM customer
WHERE status = 'ACTIVE'
ORDER BY created_at DESC, customer_id DESC
LIMIT 20 OFFSET 5000;

\echo '=== lab38-007 BASELINE: selective join, no index on account.customer_id ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.public_id, a.account_number, a.balance_cents
FROM customer c
JOIN account a ON a.customer_id = c.customer_id
WHERE c.public_id = 'CUS-1001';
