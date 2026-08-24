# Pipeline policy

| Event | Verify | Package JAR+SHA | Deploy |
| --- | --- | --- | --- |
| pull_request | Yes | No | No |
| push main | Yes | Yes | Not yet (Lab 44) |
| tag v* | Yes | Yes | Not yet (Lab 44) |

- verify runs on every event: `mvn -B clean verify` against a postgres:16 service. PRs get fast feedback.
- package (JAR + SHA-256) only on main/tags, gated by `if: github.ref == 'refs/heads/main' || startsWith(github.ref, 'refs/tags/')`. PRs produce no artifact.
- One job doing verify+package+deploy on every PR is the risk: it wastes runners and drags deploy creds into PR runs. Split the jobs and gate package/deploy instead.
- Deploy creds and kubeconfig never live in YAML. Deploy is Lab 44.
- Synthetic fixtures only, and only in test evidence: CUS-1001, CUS-1002, correlation lab-request-001.

Scope: pre-lab policy only. The full pipeline is Lab 43.
