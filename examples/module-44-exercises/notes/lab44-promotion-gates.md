# Promotion gates

| From, To | Gate |
| --- | --- |
| test to staging | Lab 43 verify green, and jarSha256 matches the `SHA256SUMS` from the `crm-jar` artifact (digest match, no rebuild). |
| staging to prod | Staging smoke passes (readiness UP, GET /api/customers?status=ACTIVE returns 200) and a named approver signs off. |

Deploy consumes the built artifact by its jarSha256. It never runs `mvn package` again, so the bytes in prod are the bytes that passed verify.
