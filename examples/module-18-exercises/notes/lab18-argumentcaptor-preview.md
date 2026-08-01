# Lab 18 — ArgumentCaptor Preview

## Step 1 — Declare
Paper: `ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);`

## Step 2 — Verify
`verify(repo).save(captor.capture());`

## Step 3 — Assert
Assert `captor.getValue().getStatus()` is ACTIVE for Ravi (CUS-1002) after activation.

## Step 4 — Prep only
*Prepare for Lab 18; do not complete full Mockito lab now.*

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.