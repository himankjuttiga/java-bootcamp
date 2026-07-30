# Lab 14 — Mapper No-Leak Rule

## Step 1 — toDto

Map only id, fullName, status for CUS-1001 responses.

## Step 2 — Forbidden

List forbidden: password hashes, internal risk scores, raw SQL ids if different.

## Step 3 — Activate DTO

Activate request carries customerId only (+ correlation header outside body).

## Step 4 — Prep boundary

Write: *DTOs before deep service rules — Lab 15 owns transitions.*

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.