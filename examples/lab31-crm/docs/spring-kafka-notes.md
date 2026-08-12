# Lab 31 — Spring Kafka notes

## Publish path

After a successful customer create/status-change, the service calls `CustomerEventPublisher.publish(event)`, which does `kafkaTemplate.send(topic, event.customerId(), event)` to `crm.customer-events.v1`. Key = `customerId` (CUS-1001 / CUS-1002) so each customer's events keep per-partition order. The send is async; the callback logs `customer_event_published id=... partition=... offset=...`.

**Timing (DB vs Kafka):** this lab publishes *after* the write succeeds. Simple, but if the DB commits and the Kafka send fails, the consumer is never notified. Production for critical notifications should use a **transactional outbox** (write the event row in the same DB transaction; a relay publishes to Kafka). This lab does not claim dual-write atomicity.

## Idempotency

`ProcessedEventStore.markIfNew(eventId)` returns `true` only the first time an `eventId` is seen (`Set.add`). The listener checks it **before** the side effect: if the id was seen, it logs `duplicate_event_ignored` and returns, so replays (rebalance / retry / manual re-consume) never double-notify. Lab store is in-memory (`ConcurrentHashMap.newKeySet()`); production needs a durable, cross-instance store (DB unique key).

## DLT

Retry/DLT is wired in `KafkaErrorConfig`: `DefaultErrorHandler` with `FixedBackOff(500ms, 2)` plus a `DeadLetterPublishingRecoverer`. `InvalidCustomerEventException` (key != customerId) is registered as **non-retryable**, so a poison message goes straight to the dead-letter topic instead of looping.

**Naming:** Spring's `DeadLetterPublishingRecoverer` default appends `.DLT`, so dead letters land on `crm.customer-events.v1.DLT`. Lab 30 pre-created `crm.customer-events.v1.dlq`. If you want dead letters on the Lab 30 `.dlq` topic, configure the recoverer's destination resolver to target `.dlq`; otherwise document that this lab uses `.DLT`. DLT record headers identify the original topic, partition, offset, and exception.

## Runbook

```bash
# Broker (Lab 30) — only needed for manual demo, not for the tests
docker compose -f ../lab30-crm/compose.yaml up -d
cd ~/java-bootcamp/examples/lab31-crm
mvn -B test        # EmbeddedKafka — no external broker required; run twice for determinism
mvn -B spring-boot:run
# Observe logs: customer_event_published / customer_event_received / duplicate_event_ignored
```

## Tests (CustomerEventFlowTest — EmbeddedKafka)

- `publishesAndConsumesCustomerCreated` — publish Amina (CUS-1001), await the listener handles `evt-amina-1`.
- `duplicateEventIgnored` — publish Ravi (CUS-1002) twice, assert it is handled exactly once.

## Security / prod

Restrict `spring.json.trusted.packages` to `com.northstar.crm.event`. Never log full PII payloads (fixture ids + correlationId only). No secrets in event bodies.
