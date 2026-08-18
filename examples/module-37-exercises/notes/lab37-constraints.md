# Lab 37 — Constraints Checklist

## Step 1 — PK / UK

| Table | Constraint | Name |
| --- | --- | --- |
| customer | PRIMARY KEY (customer_id) | `customer_pkey` |
| customer | UNIQUE (public_id) | `customer_public_id_uk` |
| customer | UNIQUE (email) | `customer_email_uk` |
| account | PRIMARY KEY (account_id) | `account_pkey` |
| account | UNIQUE (account_number) | `account_number_uk` |
| account | FOREIGN KEY (customer_id) → customer(customer_id) | `account_customer_fk` |

## Step 2 — CHECK

```sql
CONSTRAINT customer_status_chk CHECK (status IN ('PROSPECT', 'ACTIVE', 'CLOSED'))
CONSTRAINT account_balance_chk CHECK (balance_cents >= 0)
```

The status list matches the SPA's `CustomerStatus` union from Labs 34 to 36. Two places define the
same set, so the CHECK is the authoritative one: the UI can be bypassed, the database cannot.

## Step 3 — NOT NULL

`customer`: `public_id`, `full_name`, `email`, `status`, `created_at`, `updated_at`.
`account`: `customer_id`, `account_number`, `balance_cents`, `opened_at`.

Nullable by design: nothing in these two tables. A nullable `status` would mean "we do not know
what this customer is", which is not a state the business has.

## Step 4 — SQLSTATE awareness

| SQLSTATE | Condition | Triggered by |
| --- | --- | --- |
| `23505` | unique_violation | inserting `CUS-1001` twice, or reusing an email |
| `23503` | foreign_key_violation | an account whose `customer_id` does not exist, or deleting a customer that still has accounts under RESTRICT |
| `23514` | check_violation | `status = 'PENDING'`, which is the expected answer to the predict question about a bad status |
| `23502` | not_null_violation | omitting `full_name` |

Lab 38 and Lab 39 map these to HTTP responses. `23505` becomes a 409 conflict, `23514` and `23502`
become 400 validation errors, and `23503` is usually a 409 or a 422 depending on the operation.
The Spring `GlobalExceptionHandler` from Lab 29 already has the shapes; the codes are what let it
distinguish them without parsing English error text.

### Why name every constraint

An unnamed constraint gets a generated name that varies between environments, which means:

* The error message is useless to the application. `customer_email_uk` tells the API which field
  to attach the message to; `customer_check1` tells it nothing.
* Migrations cannot reliably `ALTER TABLE ... DROP CONSTRAINT` something whose name differs on
  every machine.
* Two environments diverge silently, and a schema diff shows churn that is not real.

## Negative tests to run in the lab

| Attempt | Expected |
| --- | --- |
| Insert a second customer with `public_id = 'CUS-1001'` | `23505` on `customer_public_id_uk` |
| Insert `status = 'PENDING'` | `23514` on `customer_status_chk` |
| Insert an account with `customer_id = 999999` | `23503` on `account_customer_fk` |
| Delete Amina while her account exists | `23503`, RESTRICT holds |
| Insert a customer with no `email` | `23502` |

A constraint with no failing test is an assumption, not a control.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
