# Lab 37 — CRM Entities

## Step 1 — Entities

| Table | Purpose | In scope now |
| --- | --- | --- |
| `customer` | identity, contact, lifecycle status | yes |
| `account` | financial accounts owned by a customer | yes |
| `address` | typed postal addresses, several per customer | yes, the guide lists it |
| `customer_status_history` | audit trail of status transitions | yes, status changes must be reconstructable |

No Kafka outbox table and no JPA entities in this module. Persistence mapping is Lab 39,
query tuning is Lab 38.

## Step 2 — Attributes

`customer`

| Column | Type | Note |
| --- | --- | --- |
| `customer_id` | `BIGSERIAL` PK | surrogate, meaningless by design, never shown to users |
| `public_id` | `VARCHAR(32)` UNIQUE | the business key: `CUS-1001`, safe to print and to send over the API |
| `full_name` | `VARCHAR(200)` NOT NULL | |
| `email` | `VARCHAR(320)` UNIQUE NOT NULL | 320 is the RFC maximum, 64 local plus 1 plus 255 domain |
| `status` | `VARCHAR(32)` NOT NULL | constrained by CHECK, not by convention |
| `created_at` / `updated_at` | `TIMESTAMPTZ NOT NULL DEFAULT now()` | with time zone, always |

`account`

| Column | Type | Note |
| --- | --- | --- |
| `account_id` | `BIGSERIAL` PK | surrogate |
| `customer_id` | `BIGINT NOT NULL` FK | references the surrogate, not `public_id` |
| `account_number` | `VARCHAR(32)` UNIQUE NOT NULL | business identifier |
| `balance_cents` | `BIGINT NOT NULL DEFAULT 0` | exact integer minor units |
| `opened_at` | `TIMESTAMPTZ NOT NULL DEFAULT now()` | |

Two type decisions worth defending out loud:

* **Money is never `FLOAT` or `DOUBLE`.** Binary floating point cannot represent 0.10 exactly, so
  balances drift once you start adding them. `BIGINT` cents, as the starter uses, or
  `NUMERIC(19,4)` if fractional currency units are needed. Both are exact.
* **Timestamps are `TIMESTAMPTZ`, never plain `TIMESTAMP`.** Plain timestamps silently drop the
  offset, so a record written at 5pm in New York and one written at 5pm in London become
  indistinguishable, and daylight saving turns an hour of history ambiguous.

## Step 3 — Fixtures

| public_id | full_name | email | status | accounts |
| --- | --- | --- | --- | --- |
| `CUS-1001` | Amina Khan | amina.khan@example.com | `ACTIVE` | one |
| `CUS-1002` | Ravi Singh | ravi.singh@example.com | `PROSPECT` | none |

Ravi having no account is deliberate, not an oversight: it forces the customer to account
relationship to be optional on the account side, which the ER sketch has to express.

## Step 4 — Notes

Saved in `notes/lab37-design.md`. No real PII, no passwords. Lab credentials live in a gitignored
`.env`, never in these notes and never in SQL committed to the repo.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
