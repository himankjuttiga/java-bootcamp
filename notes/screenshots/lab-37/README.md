# Lab 37 evidence — screenshots to capture

Start PostgreSQL, then work down the list. Redact passwords in every frame: never capture a
terminal where `PGPASSWORD` or a `.env` value is visible.

| # | File | What it must show |
| - | ---- | ----------------- |
| 1 | `01-container-ready.png` | `docker compose up -d` then `docker compose logs`, showing "database system is ready to accept connections", plus `docker volume ls` listing `crm_pgdata` for the persistence proof |
| 2 | `02-crm-app-role.png` | Output of `01_create_user.sql`: the role table showing `crm_app` with `rolsuper` f, `rolcreatedb` f, `rolcreaterole` f, `rolcanlogin` t |
| 3 | `03-schema-created.png` | `02_schema.sql` output, four CREATE TABLE and three CREATE INDEX lines |
| 4 | `04-describe-tables.png` | `\d customer` and `\d account` in psql, showing columns, types and the named constraints |
| 5 | `05-seed-select.png` | The two positive SELECTs from `04_verify.sql`: Amina and Ravi, then the LEFT JOIN with Ravi's null account |
| 6 | `06-history-correlation.png` | The history row for Amina showing `PROSPECT → ACTIVE` and `lab-request-001` |
| 7 | `07-named-constraints.png` | The `pg_constraint` query output listing all 14 named constraints |
| 8 | `08-negative-tests.png` | The six `PASS` lines with SQLSTATE 23514, 23505, 23503, 23503, 23502, 23514, plus the counts proving the seeds survived |
| 9 | `09-drop-recreate.png` | `05_drop.sql` then `02_schema.sql` and `03_seed.sql` re-run from empty, with `04_verify.sql` producing the same output |
| 10 | `10-wrong-drop-order.png` | `DROP TABLE customer;` before the children, showing the dependency error naming `fk_account_customer` |

Item 10 is optional for the timed path but it is the cheapest proof that the foreign keys are real
rather than decorative.
