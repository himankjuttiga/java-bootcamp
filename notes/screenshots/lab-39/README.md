# Lab 39 evidence — screenshots

**Captured, and they live in `examples/lab39-crm/screenshots/`**, same convention as Lab 38.
The names below are the files as saved.

Docker must be running throughout: the integration tests connect to the compose container from
`examples/lab39-crm/compose.yaml` (postgres:16 on 5432). Export the datasource variables first:
`cd ~/java-bootcamp/examples/lab39-crm && set -a; source .env; set +a`.
Redact nothing except a terminal that shows a password value.

| # | File | What it must show |
| - | ---- | ----------------- |
| 1 | `hibernate.png` | Application startup log: Flyway "Successfully applied 1 migration to schema public, now at version v1", followed by Hibernate starting without a validation error |
| 2 | `validate-start-crm.png` | The same boot reaching "Started CrmApplication in ... seconds" with `ddl-auto: validate` in effect, proving every mapping agrees with the migration |
| 3 | `create-201.png` | `POST /api/customers` for Amina returning 201 with the response DTO: `publicId`, `version 0`, and no `customerId` field |
| 4 | `duplicate-409.png` | The same email posted again returning 409 with a ProblemDetail body containing `correlationId`, and no SQL, constraint name or `org.hibernate` text |
| 5 | `optimistic-409.png` | Either the `concurrentUpdatesFailTheSecondWriterInsteadOfLosingIt` test passing, or two PATCH calls where the second returns 409 |
| 6 | `paging-deterministic.png` | `GET /api/customers?status=ACTIVE&page=0&size=20` and `page=1`, showing `totalElements` and no overlapping ids between the pages |
| 7 | `paging-bounds.png` | `?size=1000000` capped to 100 rows, and `?sort=password` returning 400 |
| 8 | `IT-green.png` | `mvn -q test -Dtest=CustomerRepositoryIT` with all eight tests passing, and the HikariPool line showing the PostgreSQL 16 JDBC URL |
| 9 | `verify-green.png` | `mvn clean verify` reaching BUILD SUCCESS with the Failsafe summary |
| 10 | not captured | `git status` cleanliness is shown by the commit itself: `.gitignore` excludes `.env` and `target/`, and neither appears in the tree |

Item 4 is the one to lead with. It proves two things at once: the constraint fired, and the client
learned nothing about the schema that fired it.
