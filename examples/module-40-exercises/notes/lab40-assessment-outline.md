# Lab 40 — Outline Security Assessment

Outline for `security-assessment.md`, written in Lab 40 against `examples/lab39-crm`.

## Step 1 — Sections

| # | Section | Contents |
| - | ------- | -------- |
| 1 | Scope | what was assessed and what was not: lab39-crm at a named commit, JDK 21, Boot 3.3.5, PostgreSQL 16. Containers are Lab 41, CI is Lab 43, neither is in scope |
| 2 | Tools | Dependency-Check version pinned, profile `-Psecurity-scan`, manual SAST path review, threshold `failBuildOnCVSS 7.0` |
| 3 | Findings summary | counts by severity and by classification, linked to the triage CSV rather than restated |
| 4 | Remediations planned | what is being fixed, the version bump or code change, and the regression test that proves it |
| 5 | Residual risks | anything not fixed, with the five fields below |
| 6 | Evidence index | claim to artifact, so a peer can check each statement without asking |

## Step 2 — Check the reference

Residual-risk rows carry all five fields, and a row missing one is not an accepted risk:

| Risk | Severity | Owner | Due date | Mitigating control |
| ---- | -------- | ----- | -------- | ------------------ |
| example: transitive parser CVE not yet patched upstream | High, CVSS 8.1 | H. Juttiga | 2026-08-26 | endpoint reachable only on localhost until the bump lands |
| example: no authn or authz on `/api/customers` | Critical | H. Juttiga | 2026-08-26 | not internet-exposed; Spring Security is scheduled before Lab 41 packaging |

## Step 3 — Evidence index draft

| Claim | Artifact |
| ----- | -------- |
| Scan ran at the pinned version and threshold | `notes/screenshots/lab-40/dependency-check-report.png` |
| Findings were triaged, not suppressed | `examples/module-40-exercises/notes/lab40-triage-csv-sketch.md`, then the real CSV in the lab |
| The gate fails on a High CVE | `notes/screenshots/lab-40/gate-fail-cvss.png` |
| The gate passes after triage | `notes/screenshots/lab-40/gate-green.png` |
| The fix did not break the application | regression test name, plus `mvn clean verify` green, `examples/lab39-crm/screenshots/verify-green.png` |
| No secrets in the repository or the reports | sanitised report screenshots, `.gitignore`, `.env.example` |

## Step 4 — Scope honesty

Pre-lab outline only. The full remediation, re-scan and residual-risk sign-off are the Lab 40
timed and full paths, and no finding in this file has been measured yet.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
