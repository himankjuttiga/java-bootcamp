# Lab 33 — Component notes

## List keys

`CustomerList` keys each `<li>` with `customer.customerId` (e.g. `CUS-1001`),
never the array index. An index key ties React's identity for a list item to
its *position*, not the data it represents. If the list is later sorted or
filtered (Lab 34+ adds state), an index key causes React to reuse the wrong
DOM node for the wrong customer — form focus, scroll position, or local
component state can end up attached to the wrong card. Keying by
`customerId` keeps each card's identity stable across re-renders regardless
of order, which is what `CustomerCard`'s `aria-labelledby` and any future
per-row state depend on.

## A11y

- `StatusBadge` always renders the status as visible text (`Prospect` /
  `Active` / `Closed`), not just a background color, so the status survives
  grayscale display and is read aloud by screen readers — color alone would
  fail both.
- `CustomerCard` is an `<article aria-labelledby={headingId}>` with an `<h3>`
  tied to that id, so each card has an accessible name distinct from its
  siblings, and the email is a real `mailto:` link rather than plain text.
- `EmptyState` and `LoadingState` use `role="status"` (a polite live region)
  and `ErrorState` uses `role="alert"` (assertive), so screen reader users
  are told about list-state changes without needing to re-scan the page.
- `CustomerForm` associates every input with a `<label htmlFor>` / `id` pair
  (`Full name`, `Email`, `Status`) so `getByLabelText` — and real assistive
  tech — can find each field by its name, not by DOM position.

## Handoff to Lab 34

Lab 34 copies this project to `lab34-crm` and lifts `customers` into real
state (add/edit/remove), replacing the seed-only `useState` in `App`. Because
props here are already typed (`Customer`, `CustomerDraft`, `CustomerStatus`)
and callbacks (`onEdit`, `onChange`, `onSubmit`) are already the seams Lab 34
wires up, no component markup should need to change — only what owns the
data and calls those callbacks.
