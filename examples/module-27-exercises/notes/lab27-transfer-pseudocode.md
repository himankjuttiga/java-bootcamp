# Lab 27 — Transfer Pseudocode

## Annotation / method

```
@Transactional
TransferService.transfer(String fromId, String toId, BigDecimal amount, String correlationId):
```

One public method on the Spring service bean; the whole body is a single unit of work committed by Spring on normal return.

## Force-fail check

```
  from = load(fromId)          // e.g. ACC-1001-MAIN
  to   = load(toId)            // e.g. ACC-1001-LOYALTY
  validate: from has sufficient funds  // throw BEFORE any debit
  if (toId == "ACC-FORCE-FAIL"):
      throw new IllegalStateException("forced failure")   // RuntimeException -> rollback
```

## Money steps

```
  from.debit(amount)
  to.credit(amount)
```

Both run inside the same transaction, so a later throw rolls back debit and credit together.

## Log step

```
  write TransactionLog(fromId, toId, amount, correlationId="lab-request-001")
  // Spring commits automatically if no exception propagates
```

The log write is inside the same `@Transactional` method, so it is never persisted on the forced-failure path (no success log row after `ACC-FORCE-FAIL`).

## Answers to the prompts

- **Insufficient-funds validation should throw before the debit** — fail fast so no money moves and there is nothing to roll back.
- **No, the controller must not write `TransactionLog` directly** — the log write belongs inside the transactional `TransferService` method so it commits or rolls back with the money movement.

## Scope

Pre-lab only.
