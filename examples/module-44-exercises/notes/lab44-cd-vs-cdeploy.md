# CD vs continuous deployment

| Term | Meaning |
| --- | --- |
| Continuous delivery | Every green build is releasable, but the promote to prod is a gated, human-approved step. |
| Continuous deployment | Every green build ships to prod automatically, no manual gate. |

CRM needs approval before prod: it serves real customer data (Amina CUS-1001, Ravi CUS-1002), so a named approver signs off the staging smoke before the prod promote. We do continuous delivery, not deployment.
