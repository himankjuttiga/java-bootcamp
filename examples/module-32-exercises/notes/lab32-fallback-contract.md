# Lab 32 — Fallback Contract

`AccountSummary.unavailable(customerId)` — a truthful degraded read, never a fake success.

## Step 1 — Fields kept

- `customerId` (always echo the requested id, e.g. `CUS-1001`)
- `available = false` (explicit degraded flag)
- `status = "UNKNOWN"`
- optionally `displayName` if already known from the CRM record

## Step 2 — Fields dropped

Anything that comes only from the down dependency and would be a lie if faked: `balance`, `tier`, `lastLogin`, transaction history. Omit them (or null), do not invent zeros.

## Step 3 — API signal

**HTTP 200 with `available=false` / `degraded=true`.** Justification: the CRM request itself succeeded and returned a valid, honest partial view; the client should render the page with a degraded badge rather than treat it as a hard error. (503 is reasonable if the profile is essential and the page cannot render without it, but for an enrichment read, 200-degraded is the better UX.)

## Step 4 — User message

*"Account details are temporarily limited. Core customer info is shown; balances and tier will return shortly."*

## Predict / Debug

- **`available=true` with empty data is wrong** because it lies: the UI shows blanks/zeros as if real, so an agent might act on false "0 balance" data. The flag must say the read was degraded.
- **Same fallback for writes?** No — a fallback may return a degraded *read*, but it must never claim a *write* (create/update) succeeded when it did not; writes fail loudly or queue, they do not fall back to a fake OK.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
