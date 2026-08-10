# Lab 27 — Transaction Boundary Placement

## Place annotation on

`@Transactional` on the public `TransferService.transfer(String fromId, String toId, BigDecimal amount)` method — a public method on a Spring-managed service bean. The debit + credit + `TransactionLog` write live inside this one boundary as a single unit of work.

## Avoid

`@Transactional` on `TransferController` (or any controller method). Controllers stay thin and just delegate to the service.

## Why (one sentence)

Spring wraps the service bean in an AOP proxy, so the transaction is demarcated at the service layer where the multi-step business operation lives, keeping HTTP concerns separate from persistence and letting other adapters reuse the same transactional method.

## Self-invocation risk

Calling `this.transfer(...)` from another method in the same class bypasses the AOP proxy, so `@Transactional` is silently ignored — always invoke through the injected `TransferService` bean.

## Internal step order

Inside the boundary: load accounts (`ACC-1001-MAIN`, `ACC-1001-LOYALTY`) -> debit -> credit -> write `TransactionLog`. Happy-path evidence uses correlation id `lab-request-001`.

## Answers to the prompts

- **A private `@Transactional` method does not participate** in Spring AOP — proxy-based advice only applies to public methods called through the proxy, so the annotation is ignored on private methods.
- **Yes, SOAP and REST should both call the same** `TransferService.transfer(...)`; the transaction boundary belongs to the shared service so every adapter reuses one consistent unit of work.

## Scope

Pre-lab only.
