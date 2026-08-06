# Lab 22 — Bean Graph Skeleton

## Edges (fill TODOs)

CustomerController → CustomerService
CustomerService → CustomerRepository
CustomerService → NotificationService
Optional metrics edge: CustomerService → CustomerMetrics (carried from Lab 21, if present)

All edges are constructor parameters — they must match the real constructors so `dependency-graph.md` reflects the actual wiring.

## Unit-test construction (one line)

`new CustomerService(fakeRepo, fakeNotifier)` — construct the service directly with fakes, no Spring context required.

## Debug / design challenge

If `NotificationService` also depended on `CustomerService`, you get a **circular dependency** (A → B → A). With constructor injection Spring cannot build either bean first and fails at startup with a `BeanCurrentlyInCreationException` / circular-reference error. The fix is to break the cycle — rethink responsibilities, extract a third collaborator, or (last resort) use an event/`@Lazy` boundary.

## Predict the output / behavior

No — after the IoC refactor the graph contains **no `new` edges inside CustomerService**. The container constructs collaborators and injects them; the service only declares constructor parameters. Any remaining `new` on a dependency would mean the refactor is incomplete.

## Scope

Pre-lab only.
