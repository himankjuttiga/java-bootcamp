-- Lab 37 seed — run as crm_app after 02_schema.sql
--   psql "host=localhost port=5432 dbname=crm user=crm_app" -f database/03_seed.sql
--
-- Fixtures: Amina CUS-1001 ACTIVE with one account and one address, Ravi CUS-1002 PROSPECT
-- with neither. Ravi is the edge case that keeps the relationship 1 : 0..N.
-- No real PII: example.com addresses only.

\set ON_ERROR_STOP on

BEGIN;

-- Parents first. An account inserted before its customer fails with SQLSTATE 23503.
INSERT INTO customer (public_id, full_name, email, status) VALUES
  ('CUS-1001', 'Amina Khan', 'amina@example.com', 'ACTIVE'),
  ('CUS-1002', 'Ravi Singh', 'ravi@example.com',  'PROSPECT');

-- Resolve the parent by its immutable business id, never by a hardcoded surrogate:
-- BIGSERIAL values depend on how many times this script has run.
INSERT INTO account (customer_id, account_number, balance_cents)
SELECT customer_id, 'ACCT-1001-01', 250000
FROM customer WHERE public_id = 'CUS-1001';

INSERT INTO address (customer_id, address_type, line1, city, region, postal_code, country_code)
SELECT customer_id, 'HOME', '100 Maple St', 'Toronto', 'ON', 'M5V 2T6', 'CA'
FROM customer WHERE public_id = 'CUS-1001';

-- Amina was activated: one append-only row carrying the correlation id.
INSERT INTO customer_status_history (
  customer_id, old_status, new_status, changed_by, reason, correlation_id
)
SELECT customer_id, 'PROSPECT', 'ACTIVE', 'lab37', 'Activation', 'lab-request-001'
FROM customer WHERE public_id = 'CUS-1001';

COMMIT;
