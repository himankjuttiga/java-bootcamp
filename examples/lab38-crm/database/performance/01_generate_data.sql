-- Lab 38 — generate representative volume (>= 50k customers, 70/30 skew)
--
-- Set-based INSERT ... SELECT generate_series, not a row-by-row loop: one statement, one pass,
-- seconds instead of minutes. The GUIDE's PL/SQL FOR loop is Oracle; PostgreSQL does not need it.

\set ON_ERROR_STOP on
SET search_path = crm_perf;

BEGIN;

-- 50,000 synthetic customers.
--   status skew:  i % 10 < 7  -> ACTIVE (70%), else PROSPECT (30%)
--   created_at:   spread across the last 90 days so date-range queries have something to filter
--   email:        unique per row, fictional, example.test
INSERT INTO customer (public_id, full_name, email, status, created_at)
SELECT
  'CUS-BULK-' || lpad(i::text, 6, '0'),
  'Synthetic Customer ' || i,
  'user' || lpad(i::text, 6, '0') || '@example.test',
  CASE WHEN i % 10 < 7 THEN 'ACTIVE' ELSE 'PROSPECT' END,
  now() - make_interval(days => (i % 90), mins => (i % 1440))
FROM generate_series(1, 50000) AS g(i);

-- CRM fixtures, preserved alongside the bulk rows. The hard gate says CUS-1001 and CUS-1002
-- must survive the load, so they are inserted explicitly rather than hoped for.
INSERT INTO customer (public_id, full_name, email, status, created_at) VALUES
  ('CUS-1001', 'Amina Khan', 'amina.khan@example.test', 'ACTIVE',   now() - INTERVAL '45 days'),
  ('CUS-1002', 'Ravi Singh', 'ravi.singh@example.test', 'PROSPECT', now() - INTERVAL '30 days');

INSERT INTO account (customer_id, account_number, balance_cents)
SELECT customer_id, 'ACCT-1001-01', 250000
FROM customer WHERE public_id = 'CUS-1001';

-- One account for every 10th bulk customer, so the join has realistic cardinality on both
-- the selective side (Amina, one account) and the broad side (thousands of ACTIVE customers).
INSERT INTO account (customer_id, account_number, balance_cents)
SELECT customer_id,
       'ACCT-' || lpad(customer_id::text, 8, '0'),
       (customer_id % 500) * 1000
FROM customer
WHERE public_id LIKE 'CUS-BULK-%'
  AND customer_id % 10 = 0;

COMMIT;

-- Documented skew and fixture survival
SELECT status, count(*) AS cnt,
       round(100.0 * count(*) / sum(count(*)) OVER (), 1) AS pct
FROM customer GROUP BY status ORDER BY status;

SELECT count(*) AS customers FROM customer;
SELECT count(*) AS accounts  FROM account;

SELECT public_id, full_name, status FROM customer
WHERE public_id IN ('CUS-1001', 'CUS-1002') ORDER BY public_id;
