# Lab 31 starter — timed path (~45 minutes)

**Theme:** Spring Kafka — publish, listen, DLT, idempotency

## Activity card

| | |
| --- | --- |
| **Checkpoint** | **E** |
| **Must prove** | Publish · listen-once · error/DLT config · `mvn test` ×2 |
| **Hard gate** | Pre-lab Pass · Kafka bootstrap or EmbeddedKafka |

## 45-minute checklist

- [ ] Confirm Kafka bootstrap (Lab 30 compose or shared cluster)
- [ ] Complete `CustomerEvent` + publisher (`KafkaTemplate`, key=customerId)
- [ ] Implement `@KafkaListener` + `ProcessedEventStore` idempotency
- [ ] Wire `KafkaErrorConfig` (DefaultErrorHandler + DLT recoverer)
- [ ] Run `mvn -B test`; capture Amina event handled-once evidence

## Smoke test

```bash
mvn -B test
```

Evidence under `~/java-bootcamp/notes/screenshots/lab-31/` (redact secrets).

## Timed-path Pass criteria

| Criterion | Pass / Fail |
| --------- | ----------- |
| Publisher sends keyed event to crm.customer-events.v1 | Pass / Fail |
| Listener handles once; replay ignored via ProcessedEventStore | Pass / Fail |
| Error handler / DLT config present | Pass / Fail |
| Integration test green twice | Pass / Fail |
