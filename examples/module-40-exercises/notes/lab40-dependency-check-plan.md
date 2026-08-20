# Lab 40 — Plan Dependency-Check Gate

## Step 1 — Profile sketch

Maven profile `security-scan` in `examples/lab39-crm/pom.xml`, not active by default so a normal
`mvn clean verify` stays fast:

* plugin: `org.owasp:dependency-check-maven`, goal `check`, bound to `verify` inside the profile
* version: pinned explicitly, `10.0.4`, confirmed against the current release when the profile is
  added in Lab 40. Never a range and never unpinned, or the gate silently changes behaviour between
  two runs of the same commit
* reports: `HTML` for a human and `JSON` for the triage CSV, written to `target/dependency-check/`
* fail threshold: `failBuildOnCVSS` placeholder at `7.0`, so High and Critical break the build and
  Medium and below are recorded but do not block
* suppressions: `dependency-check-suppressions.xml`, committed and reviewed, never inline

## Step 2 — Check the reference

JDK 21, Maven Wrapper habits, run from the CRM module root:

```bash
cd ~/java-bootcamp/examples/lab39-crm
./mvnw -B -Psecurity-scan dependency-check:check
```

First run downloads the NVD data into the local Maven repository under
`~/.m2/repository/org/owasp/dependency-check-data/`, which is why it takes minutes rather than
seconds and why the cache belongs on the machine, never in the repository. Answering the predict
question: the NVD database lives in that local cache, not in `target/`, so `mvn clean` does not
force a re-download.

`NVD_API_KEY` is read from the environment only, alongside the `SPRING_DATASOURCE_*` variables
already in `.env`. An API key in `pom.xml` is the same finding as a database password in
`application.yml`, which Lab 39 already refused to commit.

## Step 3 — Suppression policy draft

Every suppression carries three fields, no exceptions:

| Field | Rule |
| ----- | ---- |
| CVE id | the exact identifier being suppressed, one per entry, never a whole dependency |
| Owner | a named person accountable for revisiting it, not a team alias |
| Expiry | an explicit date. On that date the suppression stops applying and the gate fails again |

A suppression without all three is a silent suppression, and a silent suppression fails the gate
whatever the scanner reports. Deleting the profile to go green is the same failure with extra
steps: the finding is still there, only the evidence is gone.

## Step 4 — Folder prep

Sanitised evidence paths for Lab 40, created now, filled during the lab:

| Artifact | Path |
| -------- | ---- |
| Dependency-Check HTML, sanitised | `notes/screenshots/lab-40/dependency-check-report.png` |
| JSON summary excerpt | `notes/screenshots/lab-40/dependency-check-json.png` |
| Gate failing on a High CVE | `notes/screenshots/lab-40/gate-fail-cvss.png` |
| Gate green after triage | `notes/screenshots/lab-40/gate-green.png` |

Bulky raw HTML under `target/` is already gitignored by the Lab 39 `.gitignore`. The full scan is
not run in this pre-lab, only planned.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
