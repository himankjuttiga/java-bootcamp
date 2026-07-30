# Lab 14 — Invalid Cases Catalog

## Step 1 — Create invalids

| # | Case | Field / input | Expected error (Lab 14) |
| - | ---- | ------------- | ----------------------- |
| 1 | Blank fullName on create | `fullName` empty | 400 validation error, "fullName must not be blank" |
| 2 | Missing/invalid email on create | `email` blank or malformed | 400 validation error on email |
| 3 | Unknown status value | `status` = "GOLD" (not in enum) | 400 validation error, invalid status |

## Step 2 — Activate invalids

| # | Case | Input | Expected error |
| - | ---- | ----- | -------------- |
| 4 | Activate with missing id | `customerId` blank/null | 400 validation error, id required |
| 5 | Activate unknown customer | `customerId` = CUS-9999 | 404 not-found, "Customer not found: CUS-9999 correlationId=lab-request-001" |

## Step 3 — Valid control

Control (must succeed): create a Ravi-shaped customer, PROSPECT status with a non-blank name
(`fullName` = Ravi Singh, `email` = ravi.singh@example.com, `status` = PROSPECT). This proves
the validation rejects only the bad cases, not every request.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
