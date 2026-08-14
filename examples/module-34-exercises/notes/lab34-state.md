# Lab 34 — Props vs State

## Step 1 — Scenario

Editing Amina (`CUS-1001`): name field, status dropdown, Save button.

## Step 2 — Classify

| Item | Prop or state |
| --- | --- |
| initialCustomer | prop (passed in from parent, read-only source) |
| draftName | state (changes as the user types) |
| draftStatus | state (changes as the user picks a status) |
| isSaving | state (changes while the save request is in flight) |
| onSaved callback | prop (owned by the parent, called upward) |

## Step 3 — Rule

State = data that changes over time because of user interaction in this component.

## Step 4 — Notes

Saved in `notes/lab34-state.md`.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
