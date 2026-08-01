# Lab 18 — Fill Activate Interaction Sequence TODOs

## Step 1 — Copy sequence
1) stub findById(CUS-1002) → ravi PROSPECT
2) call service.activate(…)
3) verify repo.save(customer)
4) verify notifier.notifyActivated(…)  // if present
5) assert status ACTIVE
6) ArgumentCaptor previews status field ACTIVE

## Step 2 — Fill blanks
CUS-1002, activate, save, notifyActivated, ACTIVE, ACTIVE.

## Step 3 — Captor preview
Captors prove the saved Customer actually carried ACTIVE, not merely that save was called.

## Step 4 — Self-check
Step 1 id is CUS-1002 and final status is ACTIVE.

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.