# Lab 27 — ACID Notes (Northstar CRM Transfers)

Evidence correlation id: `lab-request-001`. Seeds: `ACC-MAIN-1001` (MAIN, $1000.00), `ACC-LOYALTY-1001` (LOYALTY, $50.00). `ACC-FORCE-FAIL` is not persisted — it only triggers the rollback throw.

| Property | CRM observation in this lab |
| -------- | --------------------------- |
| Atomicity | A transfer to `ACC-FORCE-FAIL` debits `ACC-MAIN-1001`, then throws `IllegalStateException` before the credit/log. Because debit + credit + log share one `@Transactional` boundary, the debit is rolled back: `ACC-MAIN-1001` is unchanged and no `TransactionLog` row is written. Proven by `forceFailRollsBack` (MAIN before == MAIN after) and by the POST returning HTTP 500. |
| Consistency | The happy path `ACC-MAIN-1001 -> ACC-LOYALTY-1001` for 50.00 moves MAIN 1000.00 -> 950.00 and LOYALTY 50.00 -> 100.00; the pair total (1050.00) is unchanged and no balance goes negative. Proven by `happyPathMovesFunds` (5.00 unit-test variant) and the smoke curl. |
| Isolation | Default isolation (Spring `DEFAULT`, H2 READ_COMMITTED). A concurrent reader never observes a half-applied transfer (debit without its matching credit) because the intermediate state is only visible after commit. Not stress-demoed in the timed path; bonus to demonstrate two concurrent transfers. |
| Durability | Once a transfer commits, updated balances and the log row are retained by the running app. Caveat: this lab uses in-memory H2 (`jdbc:h2:mem:lab27`), so durability is demonstrated within the JVM session, not across a full process/disk restart. A file-based H2 or PostgreSQL would persist across restarts. |

## Evidence commands

```bash
# Happy path (HTTP 200, {"status":"OK"})
curl -s -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" -H "X-Correlation-Id: lab-request-001" \
  -d '{"fromAccountId":"ACC-MAIN-1001","toAccountId":"ACC-LOYALTY-1001","amount":"50.00"}'

# Forced failure (HTTP 500, MAIN unchanged, no log row)
curl -s -i -X POST http://localhost:8080/api/transfers \
  -H "Content-Type: application/json" -H "X-Correlation-Id: lab-request-001" \
  -d '{"fromAccountId":"ACC-MAIN-1001","toAccountId":"ACC-FORCE-FAIL","amount":"10.00"}'

# Tests
mvn -B test   # expect Tests run: 2, BUILD SUCCESS
```

The account entity has fields `id`, `customerId`, `type`, `balance` — there is no `status` field, so no status-based invariants are claimed.
