# Lab 31 prep checklist

## Earlier exercise files present?

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/lab31-spring-kafka.md | yes |
| notes/lab31-listener-sketch.md | yes |
| notes/lab31-todos.md | yes |
| notes/lab31-error-dlt-notes.md | yes |
| notes/lab31-idempotency-plan.md | yes |

## Fixtures (verify)

| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE |
| CUS-1002 | Ravi Singh | PROSPECT |

Correlation: lab-request-001. Reused Lab 30 contract: topic `crm.customer-events.v1` (3 partitions), DLQ `crm.customer-events.v1.dlq`, key = customerId, groups `crm-notifications` / `crm-audit`.

## Dependencies / environment

- JDK 21 + Maven confirmed (same toolchain as Labs 26-30).
- Spring deps planned: `spring-kafka` (+ `spring-kafka-test` for `@EmbeddedKafka`).
- Lab 30 topics must exist (create `crm.customer-events.v1` + `.dlq` if the broker was reset).
- No Resilience4j / circuit breakers yet — that is Lab 32, parked.

## Scope statement

Pre-lab only — prepare for lab; do not complete full Lab 31 now.

## Self mark

Overall prep: Pass

If Fail, revisit exercise(s): the one owning the missing artifact (e.g. listener -> Ex 2, idempotency -> Ex 4).

## Predict / Debug

- **Pass with EmbeddedKafka only (no broker)?** Yes — `@EmbeddedKafka` spins up an in-memory broker for the tests, so the graded produce/consume/DLT tests can pass without the Docker broker running; the Docker broker is only needed for live CLI demos.
- **Lab 32 circuit breakers early?** Park them — Resilience4j is Lab 32; adding it now is scope creep on the Spring Kafka wiring this lab freezes.
