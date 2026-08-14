# Lab 34 — Event Handler Map

## Step 1 — Table

| Event | Handler | State updated |
| --- | --- | --- |
| Name input `onChange` | `setName(e.target.value)` | `name` |
| Status select `onChange` | `setStatus(e.target.value)` | `status` |
| Form `onSubmit` | `onSubmit` (preventDefault, validate) | `error`, then `customers` on success |
| Row `onClick` (select Amina) | `onEdit("CUS-1001")` | `selectedCustomerId`, `draft` seeded from Amina |
| Cancel button `onClick` | `onCancel` | `draft` reset, `mode` back to view |

## Step 2 — Rows

See table above — includes name onChange, status onChange, form onSubmit, and row onClick → select Amina.

## Step 3 — Derived

`isValid` is derived (`!!name.trim() && !!status`), not stored in its own `useState` — storing it separately risks it going stale relative to `name`/`status`.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
