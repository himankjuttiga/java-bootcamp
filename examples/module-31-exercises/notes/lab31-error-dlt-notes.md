# Lab 31 — Error and DLT Notes

## Step 1 — Retryable

Transient failures that may succeed on a later attempt: a network blip calling the email/notification API, a brief broker/downstream timeout, a temporary DB lock. These should be **retried** (Spring's `DefaultErrorHandler` with a backoff) before giving up.

## Step 2 — Non-retryable

Contract/data errors that will never succeed no matter how many retries: JSON missing `customerId`, unparseable payload, `eventVersion` the consumer cannot handle, or key != `customerId`. Classify these as **non-retryable** so they go to the DLT after limited (or zero) attempts instead of looping forever.

## Step 3 — Ops note

Support replays DLT records after fixing the consumer bug or the bad payload, tracing the failed record by its `correlationId` (e.g. `lab-request-001`). Replay must be idempotent (dedupe on `eventId`) so re-processing does not double-apply.

## Step 4 — No runtime

Confirmed: **I will not publish to the DLT from the CLI in this pre-lab.** DLT wiring (recoverer on the listener factory) is built and exercised in the graded Lab 31, not here.

## DLT naming

Spring's `DeadLetterPublishingRecoverer` default appends `.DLT` to the topic (`crm.customer-events.v1.DLT`). Lab 30 pre-created `crm.customer-events.v1.dlq`. Document which name the factory targets so the two conventions do not drift — point the recoverer at the frozen `.dlq` name if reusing Lab 30's topic.

## Predict / Debug

- **Validation error — retry or DLT?** DLT. A validation failure is deterministic, retrying wastes effort and blocks the partition; route it aside.
- **DLT empty after failures:** the recoverer is not attached to the listener container factory (or errors are still classified retryable), so failures loop instead of being published to the DLT.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
