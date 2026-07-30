# Lab 14 — Annotate Paper DTO

## Reference

| Field | Constraint idea |
| --- | --- |
| fullName | required, non-blank |
| status | optional on create; default PROSPECT |
| customerId | server-assigned or pattern CUS-#### |

## Step 2 — Paper annotations

Write pseudo `@NotBlank` / `@Pattern` names — documentation only.

## Step 3 — No Spring yet

Explicit: do not wire `@Valid` on a controller in this pre-lab.

## Step 4 — Correlation

Note correlation `lab-request-001` stays in headers/logs, not as a DTO business field.

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.