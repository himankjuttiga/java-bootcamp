# Lab 34 — Validation Messages

## Step 1 — Rules

Name required; status required; name minimum length 2.

## Step 2 — Messages

- "Name is required."
- "Name must be at least 2 characters."
- "Please choose a status."

## Step 3 — Timing

Validate on submit for Lab 34 — simplest to implement correctly with `useState` and avoids nagging the user mid-keystroke; blur-time validation can be layered on later.

## Step 4 — Server later

Lab 35 will also surface API 400 responses (server-side validation) through the same `error` state, once the form talks to the real backend.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
