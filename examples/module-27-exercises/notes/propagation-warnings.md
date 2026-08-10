# Lab 27 — Propagation Warnings

| Pattern | Risk |
| --- | --- |
| REQUIRES_NEW on log | The log write runs in its own independent transaction and commits even when the outer transfer rolls back — a success log row survives after `ACC-FORCE-FAIL`, producing a "half-committed" audit trail (money reverted, log kept). |
| Self-invocation | Calling `this.transfer(...)` from another method in the same class bypasses the AOP proxy, so `@Transactional` is silently ignored and nothing rolls back. |
| Swallow exception | Catching the exception inside the `@Transactional` method and not rethrowing lets the method return normally, so Spring commits the partial debit/credit — atomicity broken. |
| TX on controller | Boundary in the wrong layer: couples HTTP to persistence, breaks reuse from other adapters (SOAP/REST), and the intended service-level unit of work is not demarcated. |

## Lab default

`REQUIRED` (Spring's default) on `TransferService.transfer(...)`. Debit, credit, and the `TransactionLog` write all join the one transaction, so a throw on the `ACC-FORCE-FAIL` path rolls back everything together — no custom propagation needed.

## Answers to the prompts

- **Copilot's try/catch around debit that returns null: reject.** It swallows the exception so Spring never sees it propagate and commits the partial work, breaking rollback. Let the exception propagate out of the transactional method instead.
- **REQUIRED is enough for this lab** because the entire transfer is one unit of work on a single service method against one datasource; joining/creating a single transaction gives clean all-or-nothing rollback without splitting commits.

## Scope

Pre-lab only.
