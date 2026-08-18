-- Lab 38 — sargable rewrites, join strategies, deterministic paging, keyset paging.

\set ON_ERROR_STOP on
SET search_path = crm_perf;

\echo '=== lab38-009 SARGABILITY: date_trunc on the column (non-sargable) ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id FROM customer
WHERE date_trunc('day', created_at) = date_trunc('day', now() - INTERVAL '10 days');

\echo '=== lab38-010 SARGABILITY: half-open range on the bare column (sargable) ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id FROM customer
WHERE created_at >= date_trunc('day', now() - INTERVAL '10 days')
  AND created_at <  date_trunc('day', now() - INTERVAL '9 days');

\echo '=== EQUIVALENCE: both forms must return the identical id set ==='
WITH wrapped AS (
  SELECT customer_id FROM customer
  WHERE date_trunc('day', created_at) = date_trunc('day', now() - INTERVAL '10 days')
), ranged AS (
  SELECT customer_id FROM customer
  WHERE created_at >= date_trunc('day', now() - INTERVAL '10 days')
    AND created_at <  date_trunc('day', now() - INTERVAL '9 days')
)
SELECT (SELECT count(*) FROM wrapped) AS wrapped_rows,
       (SELECT count(*) FROM ranged)  AS ranged_rows,
       (SELECT count(*) FROM (SELECT customer_id FROM wrapped EXCEPT SELECT customer_id FROM ranged) d)
         AS in_wrapped_not_ranged,
       (SELECT count(*) FROM (SELECT customer_id FROM ranged EXCEPT SELECT customer_id FROM wrapped) d)
         AS in_ranged_not_wrapped;

\echo '=== lab38-011 JOIN, selective: one customer by public_id ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.public_id, a.account_number, a.balance_cents
FROM customer c
JOIN account a ON a.customer_id = c.customer_id
WHERE c.public_id = 'CUS-1001';

\echo '=== lab38-012 JOIN, broad: every ACTIVE customer that has an account ==='
EXPLAIN (ANALYZE, BUFFERS)
SELECT c.public_id, a.account_number, a.balance_cents
FROM customer c
JOIN account a ON a.customer_id = c.customer_id
WHERE c.status = 'ACTIVE';

\echo '=== DETERMINISTIC OFFSET: adjacent pages must not overlap ==='
WITH page0 AS (
  SELECT customer_id FROM customer WHERE status = 'ACTIVE'
  ORDER BY created_at DESC, customer_id DESC LIMIT 20 OFFSET 0
), page1 AS (
  SELECT customer_id FROM customer WHERE status = 'ACTIVE'
  ORDER BY created_at DESC, customer_id DESC LIMIT 20 OFFSET 20
)
SELECT (SELECT count(*) FROM page0) AS page0_rows,
       (SELECT count(*) FROM page1) AS page1_rows,
       (SELECT count(*) FROM (SELECT customer_id FROM page0 INTERSECT SELECT customer_id FROM page1) x)
         AS overlapping_ids;

\echo '=== lab38-013 KEYSET: page 2 continues after the last tuple of page 1 ==='
-- The anchor is the last row of the previous page. In an application it arrives as a request
-- parameter, so it is captured into psql variables here rather than recomputed inside the query:
-- measuring the anchor lookup as part of the page seek would hide the very cost keyset removes.
SELECT created_at AS anchor_ts, customer_id AS anchor_id
FROM customer WHERE status = 'ACTIVE'
ORDER BY created_at DESC, customer_id DESC LIMIT 1 OFFSET 19 \gset

-- Row-value comparison, not "a < x OR (a = x AND b < y)". Same meaning, and the tuple form
-- matches the composite index directly and cannot be got subtly wrong.
EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, created_at
FROM customer
WHERE status = 'ACTIVE'
  AND (created_at, customer_id) < (:'anchor_ts'::timestamptz, :anchor_id::bigint)
ORDER BY created_at DESC, customer_id DESC
LIMIT 20;

\echo '=== lab38-014 KEYSET at depth: page 251, continuing after row 5000 ==='
SELECT created_at AS deep_ts, customer_id AS deep_id
FROM customer WHERE status = 'ACTIVE'
ORDER BY created_at DESC, customer_id DESC LIMIT 1 OFFSET 4999 \gset

EXPLAIN (ANALYZE, BUFFERS)
SELECT customer_id, public_id, created_at
FROM customer
WHERE status = 'ACTIVE'
  AND (created_at, customer_id) < (:'deep_ts'::timestamptz, :deep_id::bigint)
ORDER BY created_at DESC, customer_id DESC
LIMIT 20;

\echo '=== KEYSET WALK: three pages, no duplicates, no gaps ==='
WITH p1 AS (
  SELECT customer_id, created_at FROM customer WHERE status = 'ACTIVE'
  ORDER BY created_at DESC, customer_id DESC LIMIT 20
), a1 AS (SELECT created_at, customer_id FROM p1 ORDER BY created_at, customer_id LIMIT 1),
p2 AS (
  SELECT c.customer_id, c.created_at FROM customer c, a1
  WHERE c.status = 'ACTIVE' AND (c.created_at, c.customer_id) < (a1.created_at, a1.customer_id)
  ORDER BY c.created_at DESC, c.customer_id DESC LIMIT 20
), a2 AS (SELECT created_at, customer_id FROM p2 ORDER BY created_at, customer_id LIMIT 1),
p3 AS (
  SELECT c.customer_id, c.created_at FROM customer c, a2
  WHERE c.status = 'ACTIVE' AND (c.created_at, c.customer_id) < (a2.created_at, a2.customer_id)
  ORDER BY c.created_at DESC, c.customer_id DESC LIMIT 20
)
SELECT (SELECT count(*) FROM p1) AS p1_rows,
       (SELECT count(*) FROM p2) AS p2_rows,
       (SELECT count(*) FROM p3) AS p3_rows,
       (SELECT count(DISTINCT customer_id) FROM (
          SELECT customer_id FROM p1 UNION ALL SELECT customer_id FROM p2 UNION ALL SELECT customer_id FROM p3
        ) u) AS distinct_ids_across_3_pages;
