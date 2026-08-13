# Lab 33 — A11y Checklist

## Step 1 — Semantics

Prefer `button`, `h1–h3`, `ul/li` over clickable divs.

## Step 2 — Contrast

Status colors need text ("Active" / "Prospect" / "Closed"), not color alone — a colored dot fails grayscale and screen-reader review.

## Step 3 — Keyboard

Tab order reaches the Edit/View control for both Amina and Ravi without a mouse.

## Step 4 — Checklist file

- [ ] Every interactive element is a real `button` or link, not a div with `onClick`
- [ ] Status is shown as visible text, never color alone
- [ ] Form inputs have `htmlFor`/`id`-linked labels
- [ ] Card has an accessible name (`aria-labelledby` → heading)
- [ ] Tab order reaches every action (Edit/Save/Cancel) with no traps

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
