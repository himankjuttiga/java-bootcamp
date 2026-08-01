# Lab 18 — Northstar CRM Mockito Isolation Tests

Isolates the service unit tests with Mockito: mock `CustomerRepository`, keep
`CustomerValidator` real (sharing the same mock repo), and prove interaction
contracts with `verify`, `never()`, and `ArgumentCaptor`, plus a BDDMockito
variant. The Lab 17 real-in-memory suite and the JaCoCo 0.80 service gate stay.

## Fixtures
| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE (addCustomer captor) |
| CUS-1002 | Ravi Singh | PROSPECT -> ACTIVE (stub find/save) |
| CUS-9999 | — | not-found; never().save |

Correlation id on changeStatus failures: `lab-request-001`.

## Run it
```bash
mvn -q clean test    # full suite (real-repo + Mockito + BDD)
mvn -q clean verify  # adds JaCoCo report + 0.80 service gate
mvn -q test -Dtest=CustomerServiceMockitoTest,CustomerServiceBddMockTest
```

## Suites
`CustomerServiceMockitoTest` (stub / verify / never / ArgumentCaptor),
`CustomerServiceBddMockTest` (given / then / should), plus the carried Lab 17
suites (`CustomerServiceTests`, `CustomerValidatorParameterizedTest`,
`CustomerValidatorTest`, `DefaultCustomerServiceTest`, `GlobalExceptionHandlerTest`).

## Isolation policy
Mock the repository (I/O boundary); keep the validator real; never mock the
class under test. Full rationale and stub-vs-verify guidance:
[`docs/isolation-policy.md`](docs/isolation-policy.md). AI review log:
[`copilot-notes/ai-mockito-review.md`](copilot-notes/ai-mockito-review.md) (`lab18-001`).

## Hygiene
Fresh mocks per `@BeforeEach`, no sleeps, no unused stubs, fictional emails only,
`target/` never committed.
