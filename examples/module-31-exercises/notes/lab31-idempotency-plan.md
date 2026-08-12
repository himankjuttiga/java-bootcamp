# Lab 31 — Idempotency Plan

## Step 1 — Why duplicates

1. **Producer retry** — with retries/idempotence a resend can still surface as a redelivery downstream, and at-least-once means the record may be delivered more than once.
2. **Consumer rebalance / reprocess** — if a consumer dies or the group rebalances before the offset commits, the next owner re-reads and reprocesses records already handled. Manual `--from-beginning` replay does the same.

## Step 2 — Business key

Use `eventId` (the UUID already on the Lab 30 envelope) as the idempotency key. Fallback composite if `eventId` is ever absent: `customerId + eventType + occurredAt` (e.g. `CUS-1001 + CustomerCreated + 2026-07-13T06:00:00Z`). The Kafka offset is **not** a safe id because it changes across replay and is per-partition.

## Step 3 — Store idea

Before running the side effect (send email), check a `ProcessedEventStore` for the `eventId`; if present, skip; if absent, perform the side effect and then record the `eventId` as processed.

## Step 4 — Out of scope

Paper design only — do not implement the processed-events table yet. That is built in the graded Lab 31.

## Predict / Debug

- **Mark after vs before side-effect:** marking *before* the side effect risks skipping a customer whose email never actually sent (lost work); marking *after* the side effect risks a duplicate if the crash happens between the side effect and the mark. For notifications, mark **after** (at-least-once, tolerate a rare duplicate) rather than risk never sending. True exactly-once needs the side effect + mark in one transaction.
- **Offset-only id:** a rebalance or replay resets/reuses offsets, so the same event reappears with a "new" position and gets reprocessed — offset alone cannot dedupe.

## Note

In-memory set is fine for the lab; production needs a durable store (DB table / Redis) so dedupe survives restarts.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
