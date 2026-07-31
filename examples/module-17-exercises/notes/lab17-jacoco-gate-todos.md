# Lab 17 — Fill JaCoCo Gate Narrative TODOs

## Step 1 — Copy TODOs

Tool: JaCoCo (with IntelliJ coverage as a secondary view)
Maven phase idea: bind the JaCoCo check to the verify phase (report on test)
Target line coverage % (lab goal): 70
Package to measure: com.northstar.crm.service
Gap you still expect: the unexpected 500 / fromUnexpected branch and rare defensive paths stay under-covered
Mockito depth in this pre-lab? no

## Step 2 — Fill blanks
JaCoCo runs as the coverage tool, the gate binds to verify (measuring after test), the lab goal is 70% line coverage on com.northstar.crm.service, one expected gap is the generic unexpected-failure branch, and Mockito is not used at this depth yet.

## Step 3 — AAA plan line
*AAA service tests planned; collaborators real or simple fakes until Lab 18.*

## Step 4 — Self-check
Mockito depth blank is **no**.

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.