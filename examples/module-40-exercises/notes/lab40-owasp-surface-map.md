# Lab 40 — Map CRM Attack Surfaces

## Reference

| Surface | OWASP theme | Example |
| --- | --- | --- |
| Customer GET/PUT API | Broken access control | Agent reads CUS-1001 |
| Search query params | Injection | Name/email filters |
| pom.xml deps | Vulnerable components | Transitive CVE |
| application.yml secrets | Security misconfiguration | DB password in Git |
| Actuator endpoints | Security misconfiguration | Unprotected /env |

## Step 1 — Inventory touchpoints

Target: `examples/lab39-crm`. Fixtures CUS-1001 (Amina, ACTIVE) and CUS-1002 (Ravi, PROSPECT).

| # | Surface | Where | Holds |
| - | ------- | ----- | ----- |
| 1 | `GET /api/customers/{publicId}` | `CustomerController#get` | PII |
| 2 | `GET /api/customers` list with `sort`, `size` | `CustomerController#list` | PII in bulk |
| 3 | `POST /api/customers` | `CustomerController#create` | PII |
| 4 | `PATCH /api/customers/{publicId}/status` | `CustomerController#changeStatus` | IDs |
| 5 | Authn/authz layer, absent: no security starter in `pom.xml` | all routes anonymous | n/a |
| 6 | JPA/SQL | `CustomerRepository`, `CustomerService` | PII and IDs |
| 7 | Maven dependencies | `pom.xml` | n/a |
| 8 | Datasource credentials | `application.yml`, `.env` | secret |
| 9 | Log sink on the error path | `ApiExceptionHandler` | PII, via Hibernate's message |

Kafka is not wired into this application yet.

## Step 2 — Check the reference

| OWASP theme | Status here |
| --- | --- |
| Injection | queries bind parameters, `sort` is allow-listed, no live gap |
| Broken access control | nothing authenticates or authorises any route |
| Security misconfiguration | credentials are env-only; Actuator is not on the classpath |
| Vulnerable components | unmeasured until the Lab 40 scan |
| Logging and monitoring failures | exception logging may write a bound email to the log |

## Step 3 — Rank top three

| Rank | Surface | Business impact |
| - | ------- | --------------- |
| 1 | No authn or authz on any route | anyone reaching the port lists every customer and changes Ravi's status |
| 2 | Vulnerable components | an unpatched transitive CVE is reachable through those same anonymous endpoints |
| 3 | PII in the log sink | Amina's email lands in a log with different retention and audience than the database |

Dependency-Check alone would report zero for rank 1, the most severe item here.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
