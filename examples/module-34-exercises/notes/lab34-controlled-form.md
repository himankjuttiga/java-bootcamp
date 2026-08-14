# Lab 34 — Controlled Form Sketch

## Reference

| UI piece | State field |
| --- | --- |
| Name input | name |
| Status select | status |
| Error text | error |
| Submit disabled | isValid derived |

## Step 2 — Flow

1. Render — input `value={name}` reads current state.
2. `onChange` fires — `setName(e.target.value)` updates state.
3. Validate — on submit, check `name.trim()` and `status`, set `error` if invalid.
4. `onSubmit` — preventDefault, and only if valid, hand the draft up via callback (logged with `lab-request-001` for now).

## Step 3 — Fixture

Example draft: name `Ravi Singh`, status `ACTIVE` before submit — the server assigns `CUS-1002` later (Lab 35); in-memory only for now.

## Step 4 — Uncontrolled note

Uncontrolled refs (`defaultValue` + `ref`) are out of scope for this lab path — every input here is controlled by state.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
