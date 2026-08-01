# AI Mockito Review Log

## Policy
Copilot may draft mock setups, but no stub or verification is committed until it
passes the checklist. Entries marked `manual` were written by hand.

## Checklist (applied to every accepted draft)
1. Did it mock the class under test (`DefaultCustomerService`)? Reject if yes — mock collaborators only.
2. Are stubs minimal (no unused `when` / `given` that would trip strict stubbing)?
3. Does verification match the real validator's call order (it also reads `existsById` / `existsByEmail`)?
4. Any `Thread.sleep` or real database / real Map in a "unit" test? Reject.
5. Does `mvn -q test` pass after accepting?

## Entries
### lab18-001 — manual
- Scope: `CustomerServiceMockitoTest` (stub/verify/never/ArgumentCaptor) and
  `CustomerServiceBddMockTest` (given/then/should).
- Method: written by hand against the shared-mock-repo pattern; Copilot not used.
- Risk called out: a naive draft would `@Mock DefaultCustomerService` (mocking the
  SUT) or leave an unused `when(...)` stub that trips `UnnecessaryStubbingException`.
  Both were avoided: only `CustomerRepository` is mocked, and each stub is used by
  the path under test.
- Result: full suite green; not-found path verified with `never().save(any())`.
