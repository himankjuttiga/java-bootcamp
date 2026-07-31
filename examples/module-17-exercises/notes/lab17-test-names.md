# Lab 17 — Expressive Test Names

## Step 1 — Pattern
Use methodName_state_expectedOutcome style: the method under test, the fixture state, then the expected result.

## Step 2 — Examples
- changeStatus_aminaAlreadyActive_rejectsIllegalTransition   (CUS-1001 ACTIVE -> PROSPECT throws 409)
- changeStatus_raviProspect_activatesSuccessfully            (CUS-1002 PROSPECT -> ACTIVE returns ACTIVE)
- changeStatus_customerCus9999Missing_throwsNotFound         (CUS-9999 throws 404)

## Step 3 — Anti-name
Reject vague names like `test1`, `testActivate`, or `changeStatusTest`. They encode neither the fixture state nor the expected outcome, so a failure name tells you nothing.

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.