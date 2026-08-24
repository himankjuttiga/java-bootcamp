-- Lab 39 — V1: CRM schema owned by Flyway.
--
-- Column names match Lab 37 (examples/lab37-crm) so the JPA mappings, the SQL from Lab 38 and
-- this migration all describe the same tables. One addition: a `version` column, which Lab 37
-- had no need for because optimistic locking arrives in this lab.
--
-- Edits to this file after it has been applied anywhere are a checksum mismatch, not a fix.
-- The forward change is V2__*.sql.

CREATE TABLE customer (
  customer_id   BIGSERIAL PRIMARY KEY,
  public_id     VARCHAR(32)  NOT NULL,
  full_name     VARCHAR(200) NOT NULL,
  email         VARCHAR(320) NOT NULL,
  status        VARCHAR(32)  NOT NULL,
  version       BIGINT       NOT NULL DEFAULT 0,
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
  CONSTRAINT uk_customer_public UNIQUE (public_id),
  CONSTRAINT uk_customer_email  UNIQUE (email),
  CONSTRAINT ck_customer_status CHECK (status IN ('PROSPECT', 'ACTIVE', 'CLOSED'))
);

CREATE TABLE account (
  account_id      BIGSERIAL PRIMARY KEY,
  customer_id     BIGINT      NOT NULL,
  account_number  VARCHAR(32) NOT NULL,
  balance_cents   BIGINT      NOT NULL DEFAULT 0,
  version         BIGINT      NOT NULL DEFAULT 0,
  opened_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
  CONSTRAINT uk_account_number   UNIQUE (account_number),
  CONSTRAINT ck_account_balance  CHECK (balance_cents >= 0),
  CONSTRAINT fk_account_customer FOREIGN KEY (customer_id)
    REFERENCES customer (customer_id) ON DELETE RESTRICT
);

-- Justified by Lab 38 measurements, not by speculation:
--   status alone is ~70% ACTIVE, so the composite serves filter and sort together
--   (list page 1: 782 buffers and a Sort node, down to 23 buffers and no sort)
CREATE INDEX ix_customer_status_created
  ON customer (status, created_at DESC, customer_id DESC);

-- PostgreSQL never indexes the referencing side of a foreign key for you
-- (selective join: 50 buffers via Hash Join, down to 6 via Nested Loop)
CREATE INDEX ix_account_customer ON account (customer_id);
