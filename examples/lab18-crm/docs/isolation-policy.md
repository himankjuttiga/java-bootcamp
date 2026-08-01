# Lab 18 — Test Isolation Policy (Northstar CRM)

## Which suites use what
| Suite | Repository | Purpose |
| ----- | ---------- | ------- |
| `CustomerServiceMockitoTest` | Mockito `@Mock CustomerRepository` | Unit isolation: stub / verify / never / ArgumentCaptor |
| `CustomerServiceBddMockTest` | Mockito `@Mock` (BDDMockito) | Same isolation in given/when/then style |
| `CustomerServiceTests` (Lab 17) | Real `InMemoryCustomerRepository` | Behavioral confidence against a real collaborator |
| `CustomerValidatorParameterizedTest` | Real repo (validator is the subject) | Transition matrix |
| `GlobalExceptionHandlerTest` | none | Error mapping |

Both mocked and real-repo suites coexist deliberately: mocks prove the service's
interaction contract in isolation; the real in-memory suite proves end-to-end
behavior against a concrete collaborator.

## Mock vs keep-real
Mock the `CustomerRepository` — it is the I/O boundary. Keep `CustomerValidator`
**real** and share the same mock repository with it, so uniqueness rules
(`existsById`, `existsByEmail`) still exercise production validation logic
against stubbed data. Never mock the class under test (`DefaultCustomerService`).

## Stub vs verify
- **Stub** (`when(...).thenReturn(...)` / `given(...).willReturn(...)`) feeds
  inputs: e.g. `findById("CUS-1002")` returns Ravi (PROSPECT).
- **Verify** (`verify(...)` / `then(...).should()`) proves side-effect calls
  happened: `save(...)` was called with an ACTIVE Customer, or `never().save(...)`
  on the not-found path.
Use `ArgumentCaptor` when you need multi-field assertions on the entity that
crossed the `save` boundary.

## Correlation
`changeStatus` failures carry `correlationId` `lab-request-001`; not-found and
illegal transitions surface `BusinessException` (404 / 409) without ever calling
`save`.

## Hygiene
Fresh mocks per `@BeforeEach` (no shared static mocks); no `Thread.sleep`; no
unused stubs (strict stubbing); fictional emails only; `target/` never committed.
