-- Lab 38 — self-contained performance schema.
--
-- Same table and column names as Lab 37 (examples/lab37-crm/database/02_schema.sql), because
-- Lab 39 maps these names through JPA and they must not drift. Two deliberate differences:
--
--   1. Everything lives in schema crm_perf, not crm_app. The 50k-row load stays out of the
--      Lab 37 fixtures, so that lab's evidence remains reproducible and this one can be
--      dropped and re-run at will.
--   2. There is NO unique index on email here. Lab 37 has one; creating it up front would
--      make the baseline plan in 02_baseline.sql meaningless. 03_indexes.sql adds it, which
--      is the whole point of the before-and-after measurement.

\set ON_ERROR_STOP on

-- The schema itself is created by the database owner, not by crm_app:
--
--   psql -U <owner> -d crm -c "CREATE SCHEMA IF NOT EXISTS crm_perf AUTHORIZATION crm_app;"
--
-- crm_app deliberately has no CREATE privilege on the database (Lab 37, 01_create_user.sql),
-- so it cannot create schemas. Granting CREATE ON DATABASE to fix that would hand the
-- application role the ability to add schemas anywhere, which is exactly the privilege creep
-- the least-privilege user exists to prevent. The owner hands over one schema instead.
SET search_path = crm_perf;

DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS customer;

CREATE TABLE customer (
  customer_id           BIGSERIAL,
  public_id             VARCHAR(32)  NOT NULL,
  full_name             VARCHAR(200) NOT NULL,
  email                 VARCHAR(320) NOT NULL,
  status                VARCHAR(32)  NOT NULL DEFAULT 'PROSPECT',
  created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
  updated_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
  CONSTRAINT pk_customer        PRIMARY KEY (customer_id),
  CONSTRAINT uk_customer_public UNIQUE (public_id),
  CONSTRAINT ck_customer_status CHECK (status IN ('PROSPECT', 'ACTIVE', 'CLOSED'))
);

CREATE TABLE account (
  account_id            BIGSERIAL,
  customer_id           BIGINT       NOT NULL,
  account_number        VARCHAR(32)  NOT NULL,
  balance_cents         BIGINT       NOT NULL DEFAULT 0,
  opened_at             TIMESTAMPTZ  NOT NULL DEFAULT now(),
  CONSTRAINT pk_account          PRIMARY KEY (account_id),
  CONSTRAINT uk_account_number   UNIQUE (account_number),
  CONSTRAINT ck_account_balance  CHECK (balance_cents >= 0),
  CONSTRAINT fk_account_customer FOREIGN KEY (customer_id)
    REFERENCES customer (customer_id) ON DELETE RESTRICT
);

-- No performance indexes here on purpose. They are created, measured and challenged in
-- 03_indexes.sql and 05_cleanup_indexes.sql.
