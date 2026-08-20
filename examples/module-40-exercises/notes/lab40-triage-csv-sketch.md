# Lab 40 — Sketch Findings Triage CSV

## Reference

| Classification | Meaning |
| --- | --- |
| true_positive | Confirm and fix or accept with owner |
| false_positive | Document CPE/path mismatch |
| accepted_risk | Time-bounded, owned |
| fixed | Re-scan evidence required |

## Step 1 — Columns

```csv
finding_id,cve,cvss,dependency,path,classification,owner,due_date,notes
```

| Column | Holds |
| ------ | ----- |
| finding_id | stable local id, `L40-001`, so a row survives being re-sorted |
| cve | the identifier from the report |
| cvss | base score, the number the gate threshold compares against |
| dependency | `groupId:artifactId:version` of the jar that actually carries it |
| path | direct or the transitive chain that pulled it in |
| classification | one of the four values above, never blank |
| owner | named person, required for `accepted_risk` |
| due_date | required for `accepted_risk`, the date the acceptance expires |
| notes | the rationale a reviewer needs, especially for `false_positive` |

## Step 2 — Check the reference

`true_positive`, `false_positive`, `accepted_risk`, `fixed`. An `accepted_risk` row without both
owner and due_date is not an acceptance, it is a silent suppression, and the gate fails.
A `fixed` row is not believed without a re-scan showing the finding gone.

Answering the predict question: for a false-positive CPE match, keep the evidence in `notes` —
which CPE the scanner matched, which artifact is actually on the classpath, and why they differ.
Without that line the next scan re-raises it and nobody remembers the reasoning.

## Step 3 — Sample rows

Synthetic identifiers, invented for this sketch, not findings from any real scan:

```csv
finding_id,cve,cvss,dependency,path,classification,owner,due_date,notes
L40-001,CVE-2026-00001,8.1,com.example.transitive:parser-core:2.3.1,transitive via spring-boot-starter-web,true_positive,H. Juttiga,2026-08-26,reachable from request parsing on POST /api/customers; fix by bumping the managed version and re-running the scan
L40-002,CVE-2026-00002,7.5,org.example.tool:build-helper:1.4.0,direct build-time plugin dependency,false_positive,H. Juttiga,,CPE matched the runtime library of the same name; this artifact is build scope only and never ships in the application, evidence in the dependency tree output
```

Row 1 is a true positive on a transitive jar. Row 2 is a false positive with the CPE mismatch
written down, and no due_date because nothing is being accepted.

## Step 4 — CRM link

A true positive at the API layer, such as L40-001 on the request-parsing path, sits directly under
`POST /api/customers` and `GET /api/customers/{publicId}`, the routes an agent uses to open Amina's
profile at `CUS-1001`. That is why it is ranked ahead of a build-time finding of similar score.
Nothing is being remediated today; this is the sketch of how the finding will be recorded and
decided in Lab 40.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
