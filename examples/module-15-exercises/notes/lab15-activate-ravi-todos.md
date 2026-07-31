markdown
# Lab 15 — Fill Activate Ravi Pseudocode TODOs

## Step 1 — Copy pseudocode

customer = repo.findById(CUS-1002)
if customer is null → throw CustomerNotFoundException (NotFound)
if status is not PROSPECT → throw IllegalStateException (invalid transition)
set status to ACTIVE
repo.save(customer)
log correlation lab-request-001


## Step 2 — Fill blanks

- Lookup id: `CUS-1002` (Ravi)
- Missing customer: throw a NotFound domain exception
- Guard: reject if current status is not `PROSPECT`, throw IllegalState / domain exception
- New status: `ACTIVE`
- Persist: `repo.save(customer)` (or `update`)
- Trace: log correlation `lab-request-001`

## Step 3 — Repo boundary note

*Repository saves state; it does not decide PROSPECT → ACTIVE.*

## Step 4 — Self-check

Ravi (CUS-1002) starts PROSPECT and ends ACTIVE. The transition rule lives in the service;
the repository only persists the already-decided change.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.