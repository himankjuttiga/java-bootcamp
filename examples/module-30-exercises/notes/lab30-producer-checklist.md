# Lab 30 — Producer Checklist

## Step 1 — Settings list

- `acks=all`
- idempotent producer (`enable.idempotence=true`)
- `key = customerId` (e.g. CUS-1001)
- `value = JSON envelope` (versioned CustomerCreated / CustomerStatusChanged)
- bootstrap: `localhost:9092` from host, `kafka:9092` inside the Compose network

## Step 2 — Why acks=all

Wait for every in-sync replica to acknowledge the write before treating the CRM event as durable, so a `CustomerCreated` is never silently lost if the leader dies right after writing.

## Step 3 — Idempotence

The broker de-duplicates the producer's automatic retries, so a network hiccup that causes a resend does not append Amina's `CustomerCreated` twice in the log.

## Step 4 — Out of scope today

**Do not run `kafka-console-producer` in this pre-lab.** This is planning only; broker + produce happen in the graded Lab 30.

## Predict / Debug

- **acks=0 vs acks=all under broker restart:** acks=0 fire-and-forget can silently drop events during a restart/leader change; acks=all blocks until an ISR confirms, so the event survives.
- **Idempotence without a key:** idempotence stops duplicate *appends*, but without a stable key events still spread across partitions, so per-customer ordering is not guaranteed — you need `key=customerId` for that.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
