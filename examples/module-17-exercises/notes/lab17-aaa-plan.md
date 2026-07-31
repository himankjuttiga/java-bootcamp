# Lab 17 — AAA Service Tests Plan

## Step 1 — Happy path
activate Ravi PROSPECT -> ACTIVE
- Arrange: fresh repo + validator + service; add Ravi CUS-1002 as PROSPECT.
- Act: service.changeStatus("CUS-1002", ACTIVE, "lab-request-001").
- Assert: returned status is ACTIVE; findById("CUS-1002") confirms ACTIVE persisted.

## Step 2 — Not found
CUS-9999 throws not found
- Arrange: fresh service with no CUS-9999 present.
- Act: call service.changeStatus("CUS-9999", ACTIVE, "lab-request-001") inside assertThrows.
- Assert: BusinessException thrown; getStatusHint() == 404; getCorrelationId() == "lab-request-001".

## Step 3 — Illegal
illegal transition on Amina ACTIVE
- Arrange: add Amina CUS-1001 as ACTIVE.
- Act: call service.changeStatus("CUS-1001", PROSPECT, "lab-request-001") inside assertThrows.
- Assert: BusinessException thrown (409 BUSINESS_CONFLICT); findById("CUS-1001") still ACTIVE (unchanged).

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.