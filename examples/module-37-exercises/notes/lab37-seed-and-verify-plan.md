# Lab 37 — Seed and Verify Plan

## Step 1 — Seed order

1. `customer` rows: Amina `CUS-1001` `ACTIVE`, Ravi `CUS-1002` `PROSPECT`.
2. `account` rows: one for Amina only.
3. `address` and `customer_status_history` last, both referencing `customer`.

Parents before children, always. Inserting an account first fails immediately with SQLSTATE
`23503`, `foreign_key_violation` on `account_customer_fk`, because the referenced `customer_id`
does not exist yet. That is the constraint doing its job, not a bug in the script.

The account insert resolves the parent key by `public_id` rather than hardcoding a surrogate,
because `BIGSERIAL` values depend on how many times the script has been run:

```sql
INSERT INTO account (customer_id, account_number, balance_cents)
SELECT customer_id, 'ACC-9001', 125000
FROM customer
WHERE public_id = 'CUS-1001';
```

## Step 2 — Verify SQL

```sql
-- fixtures present, with the right statuses
SELECT public_id, full_name, status
FROM customer
ORDER BY public_id;
-- expect exactly two rows: CUS-1001 ACTIVE, CUS-1002 PROSPECT

-- money read back exactly, no rounding drift
SELECT account_number, balance_cents, balance_cents / 100.0 AS balance_display
FROM account
ORDER BY account_number;
-- expect ACC-9001, 125000, 1250.00

-- timestamps carry an offset
SELECT public_id, created_at
FROM customer
ORDER BY public_id;
-- expect a +00 or local offset on each value, not a bare local timestamp
```

## Step 3 — Join check

```sql
SELECT c.public_id, c.full_name, a.account_number, a.balance_cents
FROM customer c
LEFT JOIN account a ON a.customer_id = c.customer_id
ORDER BY c.public_id;
```

Paper result:

| public_id | full_name | account_number | balance_cents |
| --- | --- | --- | --- |
| CUS-1001 | Amina Khan | ACC-9001 | 125000 |
| CUS-1002 | Ravi Singh | NULL | NULL |

`LEFT JOIN`, not `INNER JOIN`. An inner join silently drops Ravi, and a report that loses every
prospect is the kind of bug that survives for months because the numbers look plausible.

## Step 4 — Negative verify cases

| Attempt | Expected failure |
| --- | --- |
| Second customer with `public_id = 'CUS-1001'` | `23505` on `customer_public_id_uk` |
| Second customer with Amina's email | `23505` on `customer_email_uk` |
| `status = 'PENDING'` | `23514` on `customer_status_chk` |
| Account with `customer_id = 999999` | `23503` on `account_customer_fk` |
| `DELETE FROM customer WHERE public_id = 'CUS-1001'` while the account exists | `23503`, RESTRICT holds |
| Customer with `email` omitted | `23502` |

Positive seeds only prove the tables exist. These six prove the constraints exist.

## Step 5 — No execute

Nothing in this plan runs during the pre-lab. No Docker, no `psql`, no shared instance. The DDL
draft and this plan are applied in the graded lab, in order: `01_create_user.sql`,
`02_schema.sql`, `03_seed.sql`, `04_verify.sql`, with `05_drop.sql` reserved for a clean re-run.

Passwords come from a gitignored `.env`. No credential appears in any `.sql` file committed to
the repo, and no real customer PII is seeded.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
