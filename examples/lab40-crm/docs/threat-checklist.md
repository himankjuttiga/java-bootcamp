# Lab 40 — Threat checklist (OWASP-aligned)

**Scope:** CRM API serving synthetic fixtures `CUS-1001` / `CUS-1002`.

## Surfaces

| Surface | OWASP theme | Risk note | Status |
| ------- | ----------- | --------- | ------ |
| Customer lookup / list | Broken access control | Lab 39 had no authz; added Security + `requireOwner`, list is owner-scoped | Closed, lab40-002 |
| Search / filter params | Injection | derived/bound queries, `sort` allow-listed to three fields | Closed |
| Logs / error bodies | Sensitive data exposure | duplicate-email warn log can still print the email | Open, lab40-003 |
| Dependencies | Vulnerable components | Dependency-Check 10.0.4, `failBuildOnCVSS 7`; 56 Highs cut to 6 by Boot 3.5.16 / Tomcat 10.1.57 | Closed, lab40-001 |
| Secrets in config | Security misconfiguration | passwords env-only with no defaults, `.env` gitignored | Closed |

## Notes

- lab40-002 (missing object-level authz) is the confirmed finding. Evidence:
  `src/test/java/com/northstar/crm/security/ObjectOwnershipSecurityTest.java`. Triage ledger: `docs/security-findings.csv`.
- Never paste NVD API keys, tokens, or real emails into this file.
