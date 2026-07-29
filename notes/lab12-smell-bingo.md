# Lab 12 — Smell Bingo

## Step 1 — Smell list

Bingo card of five smells expected in a rushed CustomerService sketch:

1. Long method — one giant `activateProspect` doing lookup, validation, transition, and persistence.
2. Magic strings for `ACTIVE` / `PROSPECT` — status literals scattered instead of an enum or constant.
3. `==` on Strings — comparing status or IDs by reference instead of `.equals()`.
4. Mixed I/O in the domain — database or logging calls sitting inside business logic.
5. Unclear names — vague identifiers like `data`, `tmp`, `doStuff` that hide intent.

## Step 2 — Fixture tie-in

- Long method: a buried branch could skip Ravi's `CUS-1002` PROSPECT to ACTIVE transition without anyone noticing.
- Magic strings: a typo like `"ACTVE"` would silently fail to match Amina's `CUS-1001` ACTIVE status.
- `==` on Strings: `CUS-1002` read from input may not be reference-equal to a stored literal, so the lookup misses Ravi.
- Mixed I/O in domain: a persistence write firing mid-validation could leave `CUS-1001` in a half-updated state.
- Unclear names: ambiguous variables make it easy to swap Amina and Ravi and corrupt the wrong record.

## Step 3 — Priority

- ⭐ Magic strings for ACTIVE/PROSPECT — replace with an enum first.
- ⭐ `==` on Strings — switch to `.equals()` first.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.

## Fixtures (self-check)

| Customer | ID | Status |
| --- | --- | --- |
| Amina | CUS-1001 | ACTIVE |
| Ravi | CUS-1002 | PROSPECT |

Correlation ID: `lab-request-001`