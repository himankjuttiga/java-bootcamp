# Lab 33 — Props Sketch

## Reference

| Prop | Example |
| --- | --- |
| customerId | CUS-1001 |
| name | Amina Khan |
| status | ACTIVE |
| onSelect | () => void |

## Step 2 — Types

`status: 'PROSPECT' | 'ACTIVE' | 'CLOSED'`

## Step 3 — Children?

`CustomerCard` takes only typed props (`customer`, `onEdit`), no `children` — its markup is fixed and predictable, so there's nothing for a caller to inject.

## Step 4 — Anti-pattern

Do not pass the entire global store as one mega-prop — pass only the single `customer` a card needs, so it stays testable and doesn't re-render on unrelated store changes.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
