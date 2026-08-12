# Lab 30 — Why Async for CRM

## Step 1 — List sync pain

Customer service creates `CUS-1001` Amina Khan over HTTP with correlation `lab-request-001`. Three problems if it also calls email, audit, and analytics synchronously in the same request thread:

1. **Latency stacks up** — the HTTP response cannot return until email + audit + analytics all finish, so the caller waits for the slowest downstream call every time.
2. **Failure coupling** — if any one of email/audit/analytics is down or slow, the customer create itself fails or times out, even though the customer was validly created.
3. **Rigid to change** — adding a fourth side effect (e.g. search indexing) means editing and redeploying the Customer service, and each new call adds more blocking time and more failure surface.

## Step 2 — Event idea

The Customer service publishes a single `CustomerCreated` event (keyed by `customerId`) to `crm.customer-events.v1`, and the email, audit, and analytics teams each run their own consumer that reads and reacts independently, so the create returns immediately.

## Step 3 — Coupling check

**False.** The event is stored durably in Kafka once published, so the Audit consumer can process it whether or not the Customer JVM is still up; the broker, not the producer, holds the event.

## Predict / Debug

- **If Notification is down:** under sync fan-out the create fails (or blocks); under events the create still succeeds and Notification simply consumes the event later when it recovers.
- **REST-only coupling risk:** every consumer is bound to the Customer JVM's availability and API, so one slow/failed consumer degrades customer creation and every new consumer forces a producer change.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
