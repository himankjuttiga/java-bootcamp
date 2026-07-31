# Lab 17 — CsvSource Table Design

## Reference
| inputStatus | valid? |
| --- | --- |
| ACTIVE | true |
| PROSPECT | true |
| ACTVE | false |
|  | false |

## Step 2 — Extra row
| inputStatus | valid? |
| --- | --- |
| active | false |

Lowercase `active` is invalid because the enum names are case-sensitive.

As @CsvSource lines:
`"ACTIVE, true"`, `"PROSPECT, true"`, `"ACTVE, false"`, `"'', false"`, `"active, false"`
(the empty value is quoted so the blank status parses cleanly).

## Step 3 — JDK/Maven
Tests will run with JDK 21 via Maven Surefire in the timed lab.

## Step 4 — Boundary
Stubbing collaborators waits for Lab 18.

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.