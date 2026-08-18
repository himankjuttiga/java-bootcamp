-- Lab 37 schema — run as crm_app (search_path = crm_app)
--   psql "host=localhost port=5432 dbname=crm user=crm_app" -f database/02_schema.sql
--
-- Timed-path contract from the starter: column `email` (UNIQUE), `account.balance_cents BIGINT`,
-- status CHECK without SUSPENDED. The GUIDE's extended track uses `email_normalized` and
-- NUMERIC(19,2); the two are not mixed. See database/design-decisions.md.

\set ON_ERROR_STOP on

CREATE TABLE IF NOT EXISTS customer (
  customer_id           BIGSERIAL,
  public_id             VARCHAR(32)  NOT NULL,
  full_name             VARCHAR(200) NOT NULL,
  email                 VARCHAR(320) NOT NULL,
  status                VARCHAR(32)  NOT NULL DEFAULT 'PROSPECT',
  created_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
  updated_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
  CONSTRAINT pk_customer        PRIMARY KEY (customer_id),
  CONSTRAINT uk_customer_public UNIQUE (public_id),
  CONSTRAINT uk_customer_email  UNIQUE (email),
  CONSTRAINT ck_customer_status CHECK (status IN ('PROSPECT', 'ACTIVE', 'CLOSED'))
);

CREATE TABLE IF NOT EXISTS account (
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

CREATE TABLE IF NOT EXISTS address (
  address_id            BIGSERIAL,
  customer_id           BIGINT       NOT NULL,
  address_type          VARCHAR(20)  NOT NULL,
  line1                 VARCHAR(100) NOT NULL,
  line2                 VARCHAR(100),
  city                  VARCHAR(80)  NOT NULL,
  region                VARCHAR(80),
  postal_code           VARCHAR(20),
  country_code          CHAR(2)      NOT NULL DEFAULT 'CA',
  CONSTRAINT pk_address          PRIMARY KEY (address_id),
  CONSTRAINT ck_address_type     CHECK (address_type IN ('HOME', 'WORK', 'BILLING', 'OTHER')),
  CONSTRAINT fk_address_customer FOREIGN KEY (customer_id)
    REFERENCES customer (customer_id) ON DELETE CASCADE
);

-- Append only. Status changes are inserted, never updated in place, or the audit trail
-- becomes a record of the present rather than a record of what happened.
CREATE TABLE IF NOT EXISTS customer_status_history (
  history_id            BIGSERIAL,
  customer_id           BIGINT       NOT NULL,
  old_status            VARCHAR(32),
  new_status            VARCHAR(32)  NOT NULL,
  changed_by            VARCHAR(100) NOT NULL,
  reason                VARCHAR(200),
  correlation_id        VARCHAR(64),
  changed_at            TIMESTAMPTZ  NOT NULL DEFAULT now(),
  CONSTRAINT pk_cust_status_hist PRIMARY KEY (history_id),
  CONSTRAINT ck_hist_new_status  CHECK (new_status IN ('PROSPECT', 'ACTIVE', 'CLOSED')),
  CONSTRAINT fk_hist_customer    FOREIGN KEY (customer_id)
    REFERENCES customer (customer_id) ON DELETE RESTRICT
);

-- PostgreSQL indexes primary keys and UNIQUE constraints automatically, but never the
-- referencing side of a foreign key. Without these, every parent delete and every child
-- lookup scans the whole table.
CREATE INDEX IF NOT EXISTS ix_account_customer      ON account (customer_id);
CREATE INDEX IF NOT EXISTS ix_address_customer      ON address (customer_id);
CREATE INDEX IF NOT EXISTS ix_history_customer_time ON customer_status_history (customer_id, changed_at);

-- No index on public_id or email: uk_customer_public and uk_customer_email already create one.
