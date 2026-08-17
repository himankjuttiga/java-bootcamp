# Lab 35 — Error UX Copy

## Step 1 — 404

Message when `CUS-9999` is not found:

> We could not find a customer with ID CUS-9999. Check the ID and try again.

Rendered as an inline panel where the customer detail would be, not a red toast, because nothing failed technically. The user simply asked for a record that does not exist.

## Step 2 — Network

Message when the API is unreachable:

> We cannot reach the CRM service right now. Check your connection and retry in a moment.

Paired with a Retry button. `fetch` rejects with a `TypeError` here rather than returning a status, so there is no code to show and none should be shown.

## Step 3 — 400

Message when name validation fails:

> Name is required and must be between 2 and 80 characters.

Shown next to the offending field, taken from the problem-detail `detail` value, not as a generic "try again" banner. A 400 is not retryable: repeating the same request produces the same 400, so the copy has to tell the user what to change. That is the flaw in treating every failure as "something went wrong, try again".

## Step 4 — Logging

Boundary between logs and UI:

| Case | Console or server log | What the user reads |
| --- | --- | --- |
| 404 | `GET /api/customers/CUS-9999 -> 404 correlation=lab-request-001` | plain "not found" sentence |
| Network | `TypeError: Failed to fetch` plus stack | "cannot reach the CRM service" |
| 400 | `400 detail="name: size must be between 2 and 80"` | field message |
| 500 | full stack trace and correlation id | safe retry message, correlation id optional as "Reference: lab-request-001" |

`AbortError` is not a failure and gets no toast. It means we cancelled the request ourselves after an unmount or a newer query, so the UI ignores it and simply stops updating.

Stack traces, SQL fragments, class names, and raw exception text never reach the screen. `ApiError` is mapped to human copy at the component boundary; the correlation id is the only technical detail worth surfacing, and only as a reference the user can quote to support.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
