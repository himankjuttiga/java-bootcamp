# Lab 37 — PostgreSQL Design for Customers and Accounts

Module 37 · Checkpoint **E** · copied from the course starter into `examples/lab37-crm`,
then completed. Timed-path schema contract, extended with `ADDRESS` and
`CUSTOMER_STATUS_HISTORY` because deliverable 4 requires all four tables.

## Runbook

### 1. Start PostgreSQL

Shared instructor instance is the preferred path. Record host, port, database and credentials in
`.env`, which is gitignored, then skip to step 2.

Local fallback:

```bash
cd ~/java-bootcamp/examples/lab37-crm
cp .env.example .env          # then set real lab passwords in .env
docker compose up -d
docker compose logs --tail 20 postgres   # wait for "ready to accept connections"
```

`compose.yaml` pins `postgres:16` with the named volume `crm_pgdata`. Do not mix it with the
GUIDE's `postgres:17` fallback against the same volume.

### 2. Apply the scripts in order

macOS has no `psql` by default, and none is needed: the container ships one. Two helpers keep
the commands short, and connecting over the container's local socket means no password reaches
the shell or a screenshot.

```bash
set -a; source .env; set +a

pgowner() { docker compose exec -T postgres psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" "$@"; }
pgapp()   { docker compose exec -T postgres psql -U crm_app -d "$POSTGRES_DB" "$@"; }

# as the database owner: create the least-privileged app role
pgowner -v crm_app_password="$CRM_APP_PASSWORD" < database/01_create_user.sql

# everything else as crm_app
pgapp < database/02_schema.sql
pgapp < database/03_seed.sql
pgapp < database/04_verify.sql
```

With a native client installed (`brew install libpq`, or the shared instructor instance) the same
scripts run directly:

```bash
export PGPASSWORD="$CRM_APP_PASSWORD"
psql "host=localhost port=5432 dbname=$POSTGRES_DB user=crm_app" -f database/02_schema.sql
```

### 3. Prove repeatability

```bash
pgapp < database/05_drop.sql
pgapp < database/02_schema.sql
pgapp < database/03_seed.sql
pgapp < database/04_verify.sql   # identical output from empty
```

### 4. Stop, keeping the data

```bash
docker compose down        # keeps the volume
# docker compose down -v   # destructive reset, only when you mean it
```

## Script order and purpose

| Script | Runs as | Purpose |
| --- | --- | --- |
| `01_create_user.sql` | database owner | `crm_app` role, its schema, grants, no superuser |
| `02_schema.sql` | crm_app | four tables, 14 named constraints, three FK indexes |
| `03_seed.sql` | crm_app | Amina `CUS-1001` with account and address, Ravi `CUS-1002` with neither, one history row |
| `04_verify.sql` | crm_app | positive reads plus six negative constraint proofs |
| `05_drop.sql` | crm_app | children before parents, PostgreSQL syntax |

## Verified results

Ran end to end against PostgreSQL 16. Full cycle: create user, schema, seed, verify, drop,
recreate from empty, reseed, re-verify with identical output.

Positive:

```text
 public_id | full_name  |  status
-----------+------------+----------
 CUS-1001  | Amina Khan | ACTIVE
 CUS-1002  | Ravi Singh | PROSPECT

 public_id | account_number | balance_cents | balance_display
-----------+----------------+---------------+-----------------
 CUS-1001  | ACCT-1001-01   |        250000 | 2500.00
 CUS-1002  |                |               |
```

Ravi appears with nulls because the query uses `LEFT JOIN`. An inner join silently drops every
prospect, which is the kind of reporting bug that survives for months because the numbers look
plausible.

History row: `CUS-1001 | PROSPECT | ACTIVE | lab37 | lab-request-001`.

Negative, all six passing:

```text
PASS  check_violation       SQLSTATE 23514 on ck_customer_status
PASS  unique_violation      SQLSTATE 23505 on uk_customer_email
PASS  foreign_key_violation SQLSTATE 23503 on fk_account_customer
PASS  ON DELETE RESTRICT    SQLSTATE 23503 on fk_account_customer
PASS  not_null_violation    SQLSTATE 23502 on customer.email
PASS  check_violation       SQLSTATE 23514 on ck_account_balance
```

Seeds intact afterwards: 2 customers, 1 account.

Wrong drop order, captured deliberately:

```text
ERROR:  cannot drop table customer because other objects depend on it
DETAIL:  constraint fk_account_customer on table account depends on table customer
```

## Checkpoints

### Checkpoint A — Design + runtime

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | ER cardinalities + identifier decisions documented | Pass — `database/er-diagram.md`, `database/design-decisions.md` |
| 2 | PostgreSQL ready on `crm` with a persistent volume | Pass — `compose.yaml`, volume `crm_pgdata` |
| 3 | `crm_app` least-privilege user created | Pass — no superuser, createdb or createrole |

### Checkpoint B — Schema

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | CUSTOMER / ACCOUNT / ADDRESS / HISTORY with named constraints | Pass — 14 named constraints listed by `04_verify.sql` |
| 2 | Exact money type; `TIMESTAMPTZ` audit columns | Pass — `balance_cents BIGINT` per the starter contract, every timestamp `TIMESTAMPTZ` |
| 3 | FK indexes created | Pass — three, none duplicating a unique index |

### Checkpoint C — Data + proofs

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | Amina with account, Ravi without | Pass |
| 2 | History correlation `lab-request-001` | Pass |
| 3 | Negative tests recorded; drop/recreate works | Pass — six SQLSTATE proofs, full cycle re-run clean |

### Checkpoint D — Hygiene

| # | Confirm | Notes |
| - | ------- | ----- |
| 1 | Passwords only in `.env`, not Git | Pass — `.env` gitignored, scripts take the password as a psql variable |
| 2 | Design notes + screenshots | Notes Pass; screenshots pending, see `notes/screenshots/lab-37/README.md` |
| 3 | README documents connect + script order | Pass — this file |

## Failure experiments

| # | Experiment | Observed | Restore |
| - | ---------- | -------- | ------- |
| 1 | `status = 'UNKNOWN'` | `23514` on `ck_customer_status` | rolled back by the DO block |
| 2 | Duplicate `amina@example.com` | `23505` on `uk_customer_email` | rolled back |
| 3 | Account for a missing customer | `23503` on `fk_account_customer` | rolled back |
| 4 | Delete Amina while she owns an account | `23503`, RESTRICT holds | rolled back |
| 5 | Customer with no email | `23502` | rolled back |
| 6 | Negative balance | `23514` on `ck_account_balance` | rolled back |
| 7 | `DROP TABLE customer` before children | dependency error naming all three FKs | run `05_drop.sql` in order |

## Security and production review

* **Untrusted inputs:** every value arriving from the application. The database is never exposed
  to the browser; the chain is React → Spring → PostgreSQL with no shortcuts.
* **Enforcement:** constraints in the database, validation and authorization in Spring, UX checks
  in React. Three layers, each assuming the one above it failed.
* **Sensitive values:** database passwords live in `.env` only. Seeds use `example.com`, no real
  PII. The data volume is never committed.

## Reflection

1. **Which design decision most affected correctness?** Separating `customer_id` from `public_id`.
   Keying foreign keys on an immutable surrogate means a customer can correct their email or the
   business can renumber public ids without rewriting a single child row.
2. **What evidence proves it works?** Six negative tests reporting the exact SQLSTATE for each
   constraint class, and a full drop, recreate, reseed, re-verify cycle producing identical
   output from an empty database. Positive seeds alone would only prove the tables exist.
3. **Which failure was hardest to diagnose?** psql does not substitute `:variables` inside
   dollar-quoted blocks, so the password variable in an idempotent `DO` block failed with a bare
   syntax error pointing at a colon. The fix was a psql `\if` conditional, which keeps the
   password out of the file entirely.
