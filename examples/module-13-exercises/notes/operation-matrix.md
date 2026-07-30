# Lab 13 — Operation Matrix

## Step 1 — GetCustomer

In: customerId; Out: CUS-1001, Amina, ACTIVE; Fault: NotFound when the id is unknown (e.g. `CUS-9999`), `soap:Client`, correlation `lab-request-001`

## Step 2 — ActivateCustomer

In: customerId (+ correlation header idea); Out: new status; Fault: invalid transition.

## Step 3 — Happy path

Note Activate on CUS-1002 Ravi PROSPECT → ACTIVE as the design happy path.

## Step 4 — Prep only

Write: *Design only — do not complete full Lab 13 build.*

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.