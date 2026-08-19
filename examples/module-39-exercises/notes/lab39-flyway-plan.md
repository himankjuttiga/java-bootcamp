# Lab 39 — Flyway Plan

## Step 1 — Version file

`src/main/resources/db/migration/V1__crm_schema.sql`

The naming is not decoration: `V` for a versioned migration, `1` for the version, two underscores,
then a description. `R__` prefixes repeatable migrations, `U__` undo. Flyway records each applied
file in `flyway_schema_history` with its checksum.

## Step 2 — Content

`V1__crm_schema.sql` carries the Lab 37 tables plus the one column JPA needs:

```sql
CREATE TABLE customer (
  customer_id   BIGSERIAL PRIMARY KEY,
  public_id     VARCHAR(32)  NOT NULL UNIQUE,
  full_name     VARCHAR(200) NOT NULL,
  email         VARCHAR(320) NOT NULL UNIQUE,
  status        VARCHAR(32)  NOT NULL,
  version       BIGINT       NOT NULL DEFAULT 0,
  created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
  updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
  CONSTRAINT ck_customer_status CHECK (status IN ('PROSPECT', 'ACTIVE', 'CLOSED'))
);

CREATE TABLE account (
  account_id      BIGSERIAL PRIMARY KEY,
  customer_id     BIGINT NOT NULL REFERENCES customer (customer_id),
  account_number  VARCHAR(32) NOT NULL UNIQUE,
  balance_cents   BIGINT NOT NULL DEFAULT 0,
  version         BIGINT NOT NULL DEFAULT 0,
  opened_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

Two alignment notes to carry into the lab:

1. **`version` is new.** The Lab 37 schema has no `version` column, because optimistic locking did
   not exist yet. `@Version` needs one, so it appears here. If the lab ran against the Lab 37
   schema instead of its own migration, `ddl-auto: validate` would fail at boot with a missing
   column, which is validate doing its job.
2. **Indexes from Lab 38 belong in a migration too.** The starter leaves a TODO for the
   `(status, created_at)` index. Anything justified by Lab 38's measurements should land as
   `V2__performance_indexes.sql` rather than being created by hand, or the next machine to run the
   app gets a different schema.

The constraint names from Lab 37 are worth preserving here as well, since `ApiExceptionHandler`
maps a violation to a field by reading the constraint name.

## Step 3 — Why Flyway

Schema changes become versioned, ordered and repeatable: every machine, CI runner and environment
applies the same files in the same order and ends in the same state, and `flyway_schema_history`
records what ran, when, and with what checksum. A schema you can rebuild from an empty database is
a schema you can trust; one that exists because someone once ran DDL by hand is not.

It also makes the schema reviewable. A migration arrives in a pull request next to the entity
change that needs it, so a reviewer can see both halves at once.

## Step 4 — Anti-patterns

**`ddl-auto: create-drop` or `update` in a shared environment.** `create-drop` deletes the schema
on shutdown, which is data loss dressed as convenience. `update` never drops or renames anything,
so it silently accumulates orphan columns and cannot express a data migration. Neither leaves a
record of what changed. Use `validate` with Flyway and let boot fail loudly on drift.

**Mixing Flyway with `ddl-auto: update`.** Two systems then believe they own the schema. Hibernate
alters a table Flyway does not know about, the next migration collides with an object it did not
create, and the history table no longer describes reality. Pick one owner: Flyway.

**Editing an applied migration.** Flyway compares the file's checksum against
`flyway_schema_history` and refuses to start on a mismatch. That is the safety net working: the
file already ran elsewhere, so changing it means two environments have different schemas from the
same version number.

The correct fix is a **new migration**, `V2__fix_whatever.sql`, that makes the change forward.
`flyway repair` only rewrites the checksum, so it is right when a file was reformatted with no
semantic change, and wrong as a way to smuggle in an edit. Never delete rows from
`flyway_schema_history` by hand.

## Step 5 — Test strategy

Integration tests run against real PostgreSQL through Testcontainers, not H2 in PostgreSQL
compatibility mode. H2 accepts SQL PostgreSQL rejects, lacks `TIMESTAMPTZ` semantics, and behaves
differently on constraint violations, so a green H2 test can hide the exact defect the lab is about.
Same engine, same version as production, or the test proves nothing about production.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
