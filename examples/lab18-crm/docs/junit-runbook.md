# Lab 17 — JUnit Runbook (Northstar CRM Service Tests)

## How to run
```bash
mvn -q test          # run all unit tests
mvn -q clean verify  # run tests + JaCoCo report + coverage gate
mvn -q test -Dtest=CustomerServiceTests
mvn -q test -Dtest=CustomerValidatorParameterizedTest
```
Always use `clean verify` so the JaCoCo agent is attached; without `clean` the
gate may be skipped.

## Test classes
| Class | Role |
| ----- | ---- |
| `CustomerServiceTests` | Service use cases: happy path, duplicate, illegal transition, not-found |
| `CustomerValidatorParameterizedTest` | Legal/illegal transition matrix via `@CsvSource` |
| `CustomerValidatorTest` | Direct validator unit tests (carried from Lab 16) |
| `DefaultCustomerServiceTest` | Service transition + not-found behavior |
| `GlobalExceptionHandlerTest` | Error mapping (carried from Lab 16) |

## Coverage goal
JaCoCo `check` binds to `verify` with a PACKAGE rule on
`com.northstar.crm.service`: LINE `COVEREDRATIO` minimum **0.80**. Observed ratio
is ~0.97. Report: `target/site/jacoco/index.html` (not committed).

## Deliberate gate proof
Raise `<minimum>` to `0.99` and run `mvn -q clean verify`: the rule fails
(0.97 < 0.99). Restore `0.80` and it returns to BUILD SUCCESS. This proves the
gate is live, not decorative.

## Copilot review policy
AI-drafted tests are accepted only after the checklist in
`copilot-notes/ai-junit-review.md`: every assertion must be able to fail,
fixtures use shared CRM ids (no random PII), no phantom Spring/JPA imports,
independent `@BeforeEach`, and `mvn -q test` green after edits.

## Hygiene
`target/` and `target/site/jacoco` are never committed. Fixtures use fictional
emails only. Tests are deterministic — no `Thread.sleep`, no shared static state.
