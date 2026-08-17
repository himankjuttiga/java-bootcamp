# Lab 35 — Fetch Flow

## Step 1 — States

`idle | loading | success | error` for the list view.

| State | Meaning | What renders |
| --- | --- | --- |
| idle | nothing requested yet | nothing, or a skeleton shell |
| loading | request in flight | spinner plus "Loading customers…" |
| success | 200 received | the customer rows, or the empty state when the array is `[]` |
| error | network fault or non-ok status | error panel with a retry button |

`success` with zero rows is not `error`, and it is not `loading`. Without a distinct loading flag an empty first paint looks identical to "no customers exist", which is the bug this table prevents.

## Step 2 — Sequence

1. Mount `App`, effect runs with `status = "loading"`.
2. `listCustomers(signal)` sends GET `/api/customers` with `X-Correlation-Id: lab-request-001`.
3. On 200: `setCustomers([Amina CUS-1001 ACTIVE, Ravi CUS-1002 PROSPECT])`, `status = "success"`.
4. On failure: `setError(message)`, `status = "error"`, customer list left untouched.
5. Fetching lives in one api layer called from a hook in `App`; individual `CustomerCard` components receive props and never fetch, so the list is requested once instead of once per card.

## Step 3 — Abort

```ts
useEffect(() => {
  const controller = new AbortController();
  listCustomers(controller.signal)
    .then((rows) => setCustomers(rows))
    .catch((err) => { if (err.name !== "AbortError") setError(err.message); });
  return () => controller.abort();
}, [query]);
```

Without abort, fast typing in the search box leaves several requests racing and the slowest response wins, so the list can show results for an earlier query. Aborting on unmount also stops `setState` after the user has navigated away.

Double POST guard on create: disable the submit button while `isSaving` is true and ignore submits when it is already true, so one click cannot create two customers.

## Step 4 — Empty

Copy when the API returns `[]`:

> No customers yet. Create your first customer to get started.

Secondary line for a filtered list:

> No customers match "…". Clear the search to see all customers.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
