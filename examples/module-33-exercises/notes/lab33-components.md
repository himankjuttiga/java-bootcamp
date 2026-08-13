# Lab 33 — Component Inventory

## Step 1 — Screen

Imagine a Customer list showing Amina and Ravi with status badges.

## Step 2 — Inventory

`App`, `CustomerList`, `CustomerCard`, `StatusBadge`, `CustomerForm`, `EmptyState`, `LoadingState`, `ErrorState`.

## Step 3 — One responsibility

- `App` — owns fixtures, composes the dashboard
- `CustomerList` — maps customers to cards, shows empty state
- `CustomerCard` — renders one customer's identity and actions
- `StatusBadge` — renders status as visible text
- `CustomerForm` — labeled inputs for a draft customer
- `EmptyState` — announces zero results
- `LoadingState` — announces a pending fetch
- `ErrorState` — announces a failed fetch

## Step 4 — Notes

All of the above are presentational (props in, callbacks out); `App` is the only piece that will hold real state once Lab 34 lifts it. Keeping markup out of `App` now means Lab 34 only has to swap where `customers` comes from, not rewrite any component.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
