# AI JUnit Review Log

## Policy
GitHub Copilot may draft candidate tests, but no suggestion is committed until it
passes the acceptance checklist. If Copilot was unavailable, entries are marked
`manual` and the tests were written by hand.

## Checklist (applied to every accepted draft)
1. Can every assertion fail if production regresses? (no `assertNotNull(service)` only)
2. Do fixtures use shared CRM ids (CUS-1001 / CUS-1002 / CUS-9999), not random PII?
3. Are there any phantom Spring / JPA / Mockito imports? (must be none in this lab)
4. Is setup independent per test via `@BeforeEach`?
5. Does `mvn -q test` pass after edits?

## Entries
### lab17-001 — manual
- Scope: `CustomerServiceTests` (happy path, duplicate, illegal transition, not-found)
  and `CustomerValidatorParameterizedTest` (transition matrix).
- Method: written by hand against the Lab 16 service/validator seams; Copilot not used.
- Weak assertion rejected: a bare `assertNotNull(activated)` after activation was
  replaced with `assertEquals(CustomerStatus.ACTIVE, activated.getStatus())` plus a
  persisted-state re-read, so the test fails if the transition does not stick.
- Result: `mvn -q test` green; service-package coverage ~0.97.
