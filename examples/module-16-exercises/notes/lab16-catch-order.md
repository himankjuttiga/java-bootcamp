# Lab 16 — Catch Order

## Step 1 — List types
NotFoundException, ConflictException, ValidationException, Exception.

## Step 2 — Order
Write the catch/handler order top-to-bottom, specific to general:

1. NotFoundException  (404 Not Found)
2. ConflictException  (409 Conflict)
3. ValidationException (400 Bad Request)
4. Exception  (500, fallback, last)

## Step 3 — Why
A broad catch first would shadow the specific domain mapping below it, collapsing precise 4xx responses into a generic 500.

## Step 4 — Prep only
*Do not complete full Lab 16 advice wiring in pre-lab.*

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.