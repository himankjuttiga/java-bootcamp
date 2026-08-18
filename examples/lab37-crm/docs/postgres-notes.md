# Lab 37 — PostgreSQL notes

## Least-privilege application user

`database/01_create_user.sql` creates `crm_app` with `LOGIN` and nothing else:

```text
rolname | rolsuper | rolcreatedb | rolcreaterole | rolcanlogin
crm_app | f        | f           | f             | t
```

It owns the `crm_app` schema and has `USAGE, CREATE` there. It has `CONNECT` on the database. It
has no rights over any other schema, and `REVOKE ALL ON SCHEMA public FROM PUBLIC` keeps it off
the shared namespace.

Why this matters more than it looks: every SQL injection that reaches the database runs with the
application's privileges. With a DBA credential, one injected statement can drop schemas, read
other tenants, or create a new superuser. With `crm_app`, the worst case is bounded by the grants
above. The blast radius is a configuration decision made before any code is written.

`ALTER ROLE crm_app SET search_path = crm_app` means unqualified table names resolve inside the
role's own schema in every future session, so the application never accidentally reads or writes
`public`.

## The browser never touches the database

The chain is React → Spring → PostgreSQL, and no link may be skipped. The SPA holds no database
credential, opens no connection, and cannot be given one: anything shipped to the browser is
readable in devtools, as Lab 36 established for `VITE_*` variables. Spring is where authorization
is enforced and where the connection pool lives.

That is also why the constraints in `02_schema.sql` matter. The UI validates for user experience,
Spring validates because the UI can be bypassed, and the database constrains because the
application can be bypassed too. Three layers, each assuming the one above it failed.

## Connecting

```bash
# as the database owner, to create the app role
psql "host=localhost port=5432 dbname=crm user=crm" -v crm_app_password="$CRM_APP_PASSWORD" \
  -f database/01_create_user.sql

# as the application role, for everything else
PGPASSWORD="$CRM_APP_PASSWORD" psql "host=localhost port=5432 dbname=crm user=crm_app"
```

Credentials come from `.env`, which is gitignored. `.env.example` is committed and carries
placeholders only.

## Verify script design

`04_verify.sql` runs its six negative tests inside `DO` blocks that catch the expected exception
and print `PASS` with the SQLSTATE. Two reasons:

1. A failed statement inside a `DO` block rolls back with the block's subtransaction, so the seed
   data survives and no savepoint bookkeeping is needed.
2. If a statement that should fail instead succeeds, the block raises and the script stops loudly.
   A negative test that silently passes when the constraint is missing is worse than no test.

The plain `psql -f` alternative aborts the transaction at the first error, which is why the
savepoint version in the GUIDE needs `ON_ERROR_STOP` handling to run unattended.

## Container notes

`compose.yaml` pins `postgres:16` with a named volume `crm_pgdata`. The GUIDE's `docker run`
fallback uses `postgres:17`. Do not point both at the same volume: a version 16 data directory
refuses to start under 17, and the error message is obscure enough to cost real time mid-lab.

`docker compose down` stops the container and keeps the volume. `docker compose down -v` deletes
the data, which is the correct command only when a deliberate reset is wanted.
