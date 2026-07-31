# Lab 16 — Failure to Status Map

## Reference
| Failure | Status idea |
| --- | --- |
| CUS-9999 not found | 404 / SOAP Client fault |
| Activate Amina illegal transition | 409 or 422 |
| Validation blank name | 400 |
| Unexpected bug | 500 (generic message) |

## Step 2 — Choose conflict
Illegal activate (ACTIVE to PROSPECT) maps to **409 Conflict**. The request is well formed and understood; it fails because it clashes with the current resource state, which is exactly what 409 signals. I reserve 422 for input that is syntactically valid but semantically unprocessable, not a state conflict.

## Step 3 — Never
Never return 200 with an error payload for these failures. A success status on a failed operation misleads consumers and breaks programmatic error handling.

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.