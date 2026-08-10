# Lab 27 — ACID for CRM Transfers

| Letter | CRM observation |
| --- | --- |
| A | A transfer of `ACC-1001-MAIN -> ACC-1001-LOYALTY` debits, credits, and writes one `TransactionLog` row as a single unit. A forced transfer to `ACC-FORCE-FAIL` throws mid-operation, so `ACC-1001-MAIN` stays $1,000.00 and no success log row is written — debit and credit roll back together. |
| C | After the happy path, balances and the log agree: `ACC-1001-MAIN` $1,000.00 -> $900.00, `ACC-1001-LOYALTY` $100.00 -> $200.00, total across the two accounts unchanged ($1,100.00), and no balance ever goes negative. |
| I | Default isolation (Spring `DEFAULT` / H2 READ_COMMITTED) is enough for Pass; a concurrent reader never sees a half-applied transfer (debit without its matching credit) mid-transaction. No custom isolation required for this lab. |
| D | Once the happy-path transfer commits, the updated balances and the log row persist. Durability caveat: this lab uses in-memory H2, so persistence is demonstrated within the running app rather than surviving a full JVM/disk restart. |

## Accounts and correlation

`CUS-1001` Amina Khan (ACTIVE), `CUS-1002` Ravi Singh (PROSPECT); accounts `ACC-1001-MAIN` ($1,000.00), `ACC-1001-LOYALTY` ($100.00), `ACC-1002-MAIN` ($250.00), synthetic sink `ACC-FORCE-FAIL`; correlation id `lab-request-001`.

## Answers to the prompts

- **A success log row after `ACC-FORCE-FAIL`** means Atomicity failed — the exception did not roll back the partial work.
- **"We used @Transactional" is not enough** evidence for Atomicity; you must show real before/after balances and the absence of a success log row after the forced failure.

## Scope

Pre-lab only.
