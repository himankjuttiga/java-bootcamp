# Lab 15 — Transition Matrix

Statuses: PROSPECT, ACTIVE, SUSPENDED, CLOSED. Policy decision: a same-status "transition"
(e.g. ACTIVE -> ACTIVE) is rejected as an invalid transition rather than a silent no-op.

## Reference

| From | To | Allowed? | Note |
| --- | --- | --- | --- |
| PROSPECT | ACTIVE | yes | Ravi activate (CUS-1002) |
| PROSPECT | CLOSED | yes | prospect never converted |
| PROSPECT | SUSPENDED | no | cannot suspend a non-active customer |
| ACTIVE | SUSPENDED | yes | temporary hold |
| ACTIVE | CLOSED | yes | offboarding |
| ACTIVE | ACTIVE | no | reject — already active (Amina CUS-1001) |
| ACTIVE | PROSPECT | no | cannot revert to prospect |
| SUSPENDED | ACTIVE | yes | reactivate |
| SUSPENDED | CLOSED | yes | close a suspended account |
| CLOSED | (any) | no | CLOSED is terminal |

## Step 2 — Amina

CUS-1001 is already ACTIVE. Per the policy above, calling activate on Amina is an
ACTIVE -> ACTIVE transition, which is rejected as an invalid transition (not a silent no-op).

## Step 3 — Illegal list

Two illegal transitions the service will throw on later:

1. ACTIVE -> PROSPECT (cannot revert an active customer to a prospect).
2. CLOSED -> ACTIVE (CLOSED is terminal; a closed account cannot be reactivated).

## Step 4 — Boundary

Exception-to-HTTP mapping (e.g. invalid transition -> 409/422) waits for Lab 16. Lab 15
only throws domain exceptions with correlation lab-request-001.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
