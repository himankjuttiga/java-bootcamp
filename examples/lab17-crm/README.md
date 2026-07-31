# Lab 17 — Northstar CRM JUnit Service Tests

Formalizes JUnit 5 testing over the Lab 16 service layer: `CustomerServiceTests`,
a parameterized transition matrix, and a JaCoCo gate of **>= 80% line coverage**
on `com.northstar.crm.service`. Collaborators are the real in-memory repository;
Mockito isolation arrives in Lab 18.

## Fixtures
| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE (illegal-transition target) |
| CUS-1002 | Ravi Singh | PROSPECT -> ACTIVE |
| CUS-9999 | — | not-found demo |

Correlation id on changeStatus failures: `lab-request-001`.

## Run it
```bash
mvn -q test          # all unit tests
mvn -q clean verify  # tests + JaCoCo report + 0.80 service gate
```
Full command reference and coverage notes: [`docs/junit-runbook.md`](docs/junit-runbook.md).

## Test suites
`CustomerServiceTests` (happy / duplicate / illegal / not-found),
`CustomerValidatorParameterizedTest` (legal + illegal transition matrix via
`@CsvSource`), plus the carried `CustomerValidatorTest`,
`DefaultCustomerServiceTest`, and `GlobalExceptionHandlerTest`.

## Coverage gate
JaCoCo 0.8.12 `check` on the `verify` phase: PACKAGE `com.northstar.crm.service`,
LINE `COVEREDRATIO` minimum 0.80 (observed ~0.97). To prove the gate is live,
raise the minimum to 0.99, watch it fail, then restore 0.80.

## AI discipline
Copilot drafts are accepted only after the checklist in
[`copilot-notes/ai-junit-review.md`](copilot-notes/ai-junit-review.md); entry
`lab17-001` records the review (manual for this run).

## Hygiene
`target/` and `target/site/jacoco` stay uncommitted; fixtures use fictional
emails only; tests are deterministic (no sleeps, no shared static state).
