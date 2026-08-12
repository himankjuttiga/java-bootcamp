# Lab 30 prep checklist

## Earlier exercise files present?

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/lab30-prelab-eda.md | yes |
| notes/lab30-topic-map.md | yes |
| notes/lab30-envelope-sketch.md | yes |
| notes/lab30-kafka-todos.md | yes |
| notes/lab30-producer-checklist.md | yes |

## Fixtures (verify)

| ID | Name | Status |
| -- | ---- | ------ |
| CUS-1001 | Amina Khan | ACTIVE |
| CUS-1002 | Ravi Singh | PROSPECT |

Correlation ID: lab-request-001. Frozen topics: `crm.customer-events.v1` (3 partitions) + `crm.customer-events.v1.dlq` (1 partition), key = customerId.

## Scope statement

Pre-lab only — prepare for lab; do not complete full Lab 30 now. No Spring Kafka yet (that is Lab 31); this lab is concepts + a Docker Compose KRaft broker.

## Self mark

Overall prep: Pass

If Fail, revisit exercise(s): whichever deliverable is missing (topics -> Ex 2, envelopes -> Ex 3).

## Predict / Debug

- **Finish Lab 30 without Docker?** No — the graded lab needs a local KRaft broker via Docker Compose, so Docker Desktop must be installed and `docker compose version` must work.
- **Starting Lab 31 listeners early?** Stop — Spring Kafka wiring is Lab 31; doing it now skips the topic/keying foundation this lab freezes.
