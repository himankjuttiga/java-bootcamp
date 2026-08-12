# Lab 30 — Event Envelope Sketch

## Step 1 — Headers

Shared envelope fields reused for every event: `eventType`, `eventVersion`, `occurredAt`, `correlationId`, `customerId`, `payload`.

## Step 2 — Amina sample (CustomerCreated)

```json
{
  "eventType": "CustomerCreated",
  "eventVersion": 1,
  "occurredAt": "2026-08-11T14:05:00Z",
  "correlationId": "lab-request-001",
  "customerId": "CUS-1001",
  "payload": {
    "fullName": "Amina Khan",
    "status": "ACTIVE"
  }
}
```

Record key = `CUS-1001` (matches `customerId`).

## Step 3 — Ravi sample (CustomerStatusChanged)

```json
{
  "eventType": "CustomerStatusChanged",
  "eventVersion": 1,
  "occurredAt": "2026-08-11T14:06:30Z",
  "correlationId": "lab-request-001",
  "customerId": "CUS-1002",
  "payload": {
    "fullName": "Ravi Singh",
    "fromStatus": "PROSPECT",
    "toStatus": "ACTIVE"
  }
}
```

Record key = `CUS-1002`.

## Step 4 — Compatibility note

Consumers must **ignore unknown payload fields** rather than fail on them. This makes the envelope forward-compatible: a v1 consumer can safely read a payload that a newer producer enriched with extra fields, and only a genuine `eventVersion` bump signals a real breaking change.

## Predict / Debug

- **Can a v2 consumer read v1 payloads?** Usually yes if changes are additive (new optional fields); a v2 consumer tolerating missing new fields reads v1 fine. A field removal/rename is breaking and is what the version bump flags.
- **PII:** keep sensitive data out of the event body where possible; never put secrets/passwords in the payload. The key must equal `data.customerId`.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
