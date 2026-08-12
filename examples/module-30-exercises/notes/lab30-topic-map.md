# Lab 30 — Topic and Key Map

## Reference

| Concept | Northstar choice |
| --- | --- |
| Main topic | crm.customer-events.v1 |
| DLQ topic | crm.customer-events.v1.dlq |
| Partitions (lab) | 3 |
| Record key | customerId (e.g. CUS-1001) |
| Future (invented) | crm.account-events.v1 |

## Step 2 — Keying reason

Keying by `CUS-1001` / `CUS-1002` means every event for one customer hashes to the same partition, and because Kafka guarantees order within a partition, that customer's events (created -> status changed -> ...) are always read in the order they happened. A null or random key would round-robin the events across the 3 partitions and lose per-customer ordering.

## Step 3 — Versioning

The `.v1` suffix lets the team introduce a breaking payload change as a new topic (`crm.customer-events.v2`) and run both in parallel: existing consumers keep reading v1 while new consumers migrate to v2, with no forced big-bang cutover. The version is baked into the contract, not guessed at runtime.

## Step 4 — DLQ trigger

Two failure cases that should land on `crm.customer-events.v1.dlq` instead of the main topic:

1. **Poison message** — the record body cannot be deserialized / fails schema validation, so no consumer can ever process it.
2. **Repeated processing failure** — a record that keeps throwing after the configured retry limit (e.g. a persistently failing downstream dependency), routed aside so it does not block the partition.

## Predict / Debug

- **Null / random key:** events spread across partitions, per-customer order is lost.
- **Two topics vs one topic + eventType:** one topic keyed by customerId keeps all of a customer's lifecycle events ordered together and simple to consume; splitting by event type into separate topics eases independent scaling/retention per type but loses cross-type ordering for a customer. Northstar uses one topic + `eventType` field.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
