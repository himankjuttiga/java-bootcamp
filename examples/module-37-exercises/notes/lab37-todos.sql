-- Lab 37 — offline DDL draft (pre-lab). Nothing here is executed until the graded lab.
-- PostgreSQL dialect only: no Oracle NUMBER, no VARCHAR2, no CASCADE CONSTRAINTS PURGE.

CREATE TABLE customer (
  customer_id   BIGSERIAL PRIMARY KEY,
  public_id     VARCHAR(32)  NOT NULL,
  full_name     VARCHAR(200) NOT NULL,
  email         VARCHAR(320) NOT NULL,
  status        VARCHAR(32)  NOT NULL,
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
  CONSTRAINT customer_public_id_uk UNIQUE (public_id),
  CONSTRAINT customer_email_uk     UNIQUE (email),
  CONSTRAINT customer_status_chk   CHECK (status IN ('PROSPECT', 'ACTIVE', 'CLOSED'))
);

CREATE TABLE account (
  account_id     BIGSERIAL PRIMARY KEY,
  customer_id    BIGINT       NOT NULL,
  account_number VARCHAR(32)  NOT NULL,
  balance_cents  BIGINT       NOT NULL DEFAULT 0,
  opened_at      TIMESTAMPTZ  NOT NULL DEFAULT now(),
  CONSTRAINT account_number_uk   UNIQUE (account_number),
  CONSTRAINT account_balance_chk CHECK (balance_cents >= 0),
  CONSTRAINT account_customer_fk FOREIGN KEY (customer_id)
    REFERENCES customer (customer_id) ON DELETE RESTRICT
);

-- PostgreSQL indexes the PK and every UNIQUE constraint automatically.
-- It does NOT index the referencing side of a foreign key.
CREATE INDEX account_customer_id_idx ON account (customer_id);
CREATE INDEX customer_status_idx     ON customer (status);

-- Seed (customers first: an account cannot reference a customer that does not exist yet)
INSERT INTO customer (public_id, full_name, email, status) VALUES
  ('CUS-1001', 'Amina Khan', 'amina.khan@example.com', 'ACTIVE'),
  ('CUS-1002', 'Ravi Singh', 'ravi.singh@example.com', 'PROSPECT');

INSERT INTO account (customer_id, account_number, balance_cents)
SELECT customer_id, 'ACC-9001', 125000
FROM customer
WHERE public_id = 'CUS-1001';

-- TODO Lab 37: run 04_verify.sql after apply (in lab)
-- TODO Lab 37: run the negative constraint tests from notes/lab37-constraints.md
-- TODO Lab 37: create the least-privileged crm_app role, password from .env, never from Git
