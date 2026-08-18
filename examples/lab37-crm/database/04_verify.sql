-- Lab 37 verification — positive reads plus negative constraint proofs.
--   psql "host=localhost port=5432 dbname=crm user=crm_app" -f database/04_verify.sql
--
-- The negative tests run inside DO blocks that catch the expected SQLSTATE and print PASS.
-- Each block's failed statement is rolled back with the block, so the seed data is untouched
-- and the script needs no savepoints and no ON_ERROR_STOP juggling. If a statement that should
-- fail succeeds instead, the block raises and the whole script stops loudly.

\set ON_ERROR_STOP on

\echo '=== POSITIVE: fixtures present ==='
SELECT public_id, full_name, status FROM customer ORDER BY public_id;

\echo '=== POSITIVE: Amina has an account, Ravi has none (LEFT JOIN keeps the prospect) ==='
SELECT c.public_id,
       a.account_number,
       a.balance_cents,
       a.balance_cents / 100.0 AS balance_display
FROM customer c
LEFT JOIN account a ON a.customer_id = c.customer_id
ORDER BY c.public_id;

\echo '=== POSITIVE: address and append-only status history with correlation id ==='
SELECT c.public_id, ad.address_type, ad.city, ad.country_code
FROM customer c JOIN address ad ON ad.customer_id = c.customer_id
ORDER BY c.public_id;

SELECT c.public_id, h.old_status, h.new_status, h.changed_by, h.correlation_id
FROM customer c JOIN customer_status_history h ON h.customer_id = c.customer_id
ORDER BY h.changed_at;

\echo '=== POSITIVE: named constraints exist ==='
SELECT conname, contype
FROM pg_constraint
WHERE connamespace = 'crm_app'::regnamespace
ORDER BY conname;

\echo '=== NEGATIVE TESTS (each must fail; seeds must survive) ==='

-- 1. CHECK violation: a status outside the allowed set. SQLSTATE 23514.
DO $$
BEGIN
  INSERT INTO customer (public_id, full_name, email, status)
  VALUES ('CUS-X', 'Bad Status', 'bad@example.com', 'UNKNOWN');
  RAISE EXCEPTION 'NEGATIVE TEST FAILED: ck_customer_status accepted UNKNOWN';
EXCEPTION WHEN check_violation THEN
  RAISE NOTICE 'PASS  check_violation      SQLSTATE % on %', SQLSTATE, 'ck_customer_status';
END $$;

-- 2. UNIQUE violation: Amina's email reused. SQLSTATE 23505.
DO $$
BEGIN
  INSERT INTO customer (public_id, full_name, email, status)
  VALUES ('CUS-DUPE', 'Dupe', 'amina@example.com', 'PROSPECT');
  RAISE EXCEPTION 'NEGATIVE TEST FAILED: uk_customer_email accepted a duplicate';
EXCEPTION WHEN unique_violation THEN
  RAISE NOTICE 'PASS  unique_violation     SQLSTATE % on %', SQLSTATE, 'uk_customer_email';
END $$;

-- 3. FK violation: an account whose customer does not exist. SQLSTATE 23503.
DO $$
BEGIN
  INSERT INTO account (customer_id, account_number, balance_cents)
  VALUES (999999, 'ACCT-ORPHAN', 0);
  RAISE EXCEPTION 'NEGATIVE TEST FAILED: fk_account_customer accepted an orphan';
EXCEPTION WHEN foreign_key_violation THEN
  RAISE NOTICE 'PASS  foreign_key_violation SQLSTATE % on %', SQLSTATE, 'fk_account_customer';
END $$;

-- 4. FK violation on delete: RESTRICT protects a customer that still owns an account.
DO $$
BEGIN
  DELETE FROM customer WHERE public_id = 'CUS-1001';
  RAISE EXCEPTION 'NEGATIVE TEST FAILED: Amina was deleted while she still owns an account';
EXCEPTION WHEN foreign_key_violation THEN
  RAISE NOTICE 'PASS  ON DELETE RESTRICT   SQLSTATE % on %', SQLSTATE, 'fk_account_customer';
END $$;

-- 5. NOT NULL violation: a customer with no email. SQLSTATE 23502.
DO $$
BEGIN
  INSERT INTO customer (public_id, full_name, status)
  VALUES ('CUS-NONULL', 'No Email', 'PROSPECT');
  RAISE EXCEPTION 'NEGATIVE TEST FAILED: email accepted NULL';
EXCEPTION WHEN not_null_violation THEN
  RAISE NOTICE 'PASS  not_null_violation   SQLSTATE % on %', SQLSTATE, 'customer.email';
END $$;

-- 6. CHECK violation: a negative balance. SQLSTATE 23514.
DO $$
BEGIN
  INSERT INTO account (customer_id, account_number, balance_cents)
  SELECT customer_id, 'ACCT-NEG', -1 FROM customer WHERE public_id = 'CUS-1001';
  RAISE EXCEPTION 'NEGATIVE TEST FAILED: ck_account_balance accepted a negative balance';
EXCEPTION WHEN check_violation THEN
  RAISE NOTICE 'PASS  check_violation      SQLSTATE % on %', SQLSTATE, 'ck_account_balance';
END $$;

\echo '=== POSITIVE: seeds intact after the negative tests ==='
SELECT count(*) AS customers FROM customer;
SELECT count(*) AS accounts  FROM account;
