# Lab 40 — Security assessment

**App:** Northstar CRM (`lab40-crm`)  
**Fixtures:** `CUS-1001`, `CUS-1002`, correlation `lab-request-001`  
**Repo:** `java-bootcamp/examples/lab40-crm` (not the course clone)  
**Scan command:** `mvn -B -Psecurity-scan dependency-check:check` + `-DnvdApiKey` from env + `-DdataDirectory` (plugin **10.0.4**)

## Summary

The Boot 3.3.5 baseline failed the gate with 56 Highs, mostly from Tomcat 10.1.31 (lab40-001).
Bumping the Boot parent to 3.5.16 and Tomcat to 10.1.57 dropped that to 6, and I accepted the last
6 with an expiry since no fixed version is out yet. Also fixed the missing object-level authz from
Lab 39 (lab40-002). Tests stay green and I never lowered the threshold.

## Before / after

| Item | Before | After |
| ---- | ------ | ----- |
| High findings (≥ CVSS 7) | 56 | 6 (0 after suppression) |
| Remediation | — | Boot 3.5.16 + Tomcat 10.1.57; regression `ObjectOwnershipSecurityTest` green |
| Suppressions | 0 | 6, expiry 2026-11-01 |

## Residual risks

| Risk | Severity | Owner | Expiry | Acceptance |
| ---- | -------- | ----- | ------ | ---------- |
| lab40-003, PII in the exception log | 5.3 | H. Juttiga | 2026-09-16 | logs local, HTTP bodies stay clean |
| lab40-004/005/006, 6 residual Highs | 7.5 | H. Juttiga | 2026-11-01 | suppressed, no fixed release yet |

## Evidence paths

- HTML report: `target/dependency-check-report.html` (sanitized excerpts in `notes/screenshots/lab-40/`)
- CSV: `docs/security-findings.csv`
