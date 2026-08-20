# Lab 40 — Draft AppSec Go/No-Go Questions

## Step 1 — Questions

| # | Question | No-go when |
| - | -------- | ---------- |
| 1 | Is every High and Critical finding either fixed or accepted with a named owner and an expiry date? | any High or Critical is open, or accepted with a blank owner or no expiry |
| 2 | Are there secrets in Git, in the scan reports, or in the evidence screenshots? | any credential, NVD key or `.env` value appears in the repository or an artifact |
| 3 | Is there a negative authorisation test proving one agent cannot read another agent's customer? | no test exists, or it only proves the happy path |
| 4 | Is the suppression policy applied, with every suppression carrying CVE id, owner and expiry? | any suppression is silent, or the profile was removed to make the build green |
| 5 | Does `mvn clean verify` still pass after the security changes? | verify is red, or was not re-run after the last fix |

## Step 2 — Check the reference

Leadership's rule for this gate: no ship on raw scanner volume, no silent suppressions, no secrets.
A count of findings is not a decision, and a green build produced by deleting the check is worse
than a red one because it also destroys the evidence.

Answering the predict question: one Critical CVE accepted with no expiry is a **no-go**. The
acceptance is not time-bounded, so nobody ever revisits it and the risk becomes permanent by
default. Answering the debug question: green verify with no triage CSV is also a no-go, because
verify proves the application works, not that the findings were decided by a person.

## Step 3 — Tie to CRM

| # | Impact on agents serving Amina and Ravi |
| - | --------------------------------------- |
| 1 | an open Critical on the request path is reachable through the same route an agent uses to open `CUS-1001`, so exploitation looks like ordinary traffic |
| 2 | a leaked datasource password is direct read access to every customer row, bypassing the API and its correlation ids entirely |
| 3 | without an object-level test, an agent handling Ravi at `CUS-1002` can also read and change Amina at `CUS-1001`, which is the finding ranked first in the surface map |
| 4 | a silent suppression means the next person cannot tell whether Amina's data is protected or the alarm was simply muted |
| 5 | a security fix that breaks `verify` takes the CRM offline for every agent, which is an availability incident caused by a security change |

**Self-mark:** Pass

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
