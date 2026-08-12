# Lab 31 — Listener Sketch

## Step 1 — Method outline

```java
@KafkaListener(topics = "crm.customer-events.v1", groupId = "crm-notifications")
void onCustomerEvent(ConsumerRecord<String, String> record) {
  String key = record.key();           // e.g. CUS-1001
  String json = record.value();        // envelope
  // parse -> read customerId; verify key.equals(customerId); then notify
}
```

## Step 2 — Second group (audit)

```java
@KafkaListener(topics = "crm.customer-events.v1", groupId = "crm-audit")
void onAuditEvent(ConsumerRecord<String, String> record) {
  // independent group -> reads the full stream for the audit trail
}
```

Same topic, different `groupId`, so both groups get their own copy of every event.

## Step 3 — Payload type

Start with `String` (raw JSON) parsed into a `JsonNode` for the timed path — it matches the Lab 30 envelopes and avoids deserializer/trusted-package config. Justification: fewest moving parts to get produce->consume green; migrate to a typed `CustomerEvent` DTO once the contract is stable.

## Step 4 — Correlation

Log `correlationId` (e.g. `lab-request-001`) at the start of each listener (ideally into MDC) so a support engineer can trace one customer request across the produce and both consumer paths.

## Predict / Debug

- **Wrong group-id:** a brand-new group id with `auto-offset-reset=earliest` re-reads all history; with `latest` it skips existing records and only sees new ones. So the wrong group id can silently reprocess everything or miss data.
- **void vs Acknowledgment:** with default (auto) ack, a `void` method commits the offset after returning normally; you switch to manual `Acknowledgment.acknowledge()` when you need to control exactly when the offset commits (e.g. only after a downstream call succeeds).

## Key check

If `record.key()` does not equal the payload's `customerId`, reject / route to the DLT rather than process a mismatched event.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
