# Failure Experiments — Lab 12

| # | Experiment | Observed | Restore |
| - | ---------- | -------- | ------- |
| 1 | Create CUS-1001 twice | Second call throws IllegalStateException: "Customer already exists: CUS-1001 correlationId=lab-request-001" | Kept duplicate detection (requireUniqueId) |
| 2 | Get unknown CUS-9999 | Throws IllegalArgumentException: "Customer not found: CUS-9999 correlationId=lab-request-001" instead of returning null | Kept exception-on-missing (requireExisting) |
| 3 | Look up new String("CUS-1001") after create | Resolves correctly via Map key / equals; old `==` code would have returned null | Kept Map-keyed storage (test: getByValueEqualIdWorks) |
| 4 | Blank customerId create | requireNonBlank throws IllegalArgumentException: "customerId must not be blank correlationId=lab-request-001" | Kept validation helper |

All four confirm the refactor's failure paths fire on purpose, with the correlation ID
attached for support tracing. Evidence: Main demo transcript + green test run (8 tests).