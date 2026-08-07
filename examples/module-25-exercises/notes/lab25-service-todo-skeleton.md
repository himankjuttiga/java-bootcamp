# Lab 25 — Service Layer Skeleton

## Constructor deps

`CustomerService(CustomerRepository repository)` — constructor injection of the repository interface, stored in a `final` field. No `new`, no field `@Autowired`.

## create TODO

`create(customer)`: check `repository.findById(id)` — if present, throw a duplicate/conflict exception; otherwise `repository.save(customer)` and return the saved customer. Optionally validate blank id first.

## get TODO

`get(id)`: `repository.findById(id).orElseThrow(...)` — throw a not-found exception (e.g. for CUS-9999). Returns the domain `Customer`.

## Forbidden in this class

No `ResponseEntity` or any HTTP/web types, no JSON/XML mapping, no direct map access, no logging of PII. The service speaks domain objects and throws domain exceptions only.

## Seeds

Repository seeds CUS-1001 (Amina, ACTIVE) and CUS-1002 (Ravi, PROSPECT).

## Debug / design challenge

Seeding of CUS-1001 lives in the **repository**, not the service. Seed/storage detail is a persistence concern; the service stays about rules. This keeps the service testable against any repository implementation.

## Predict the output / behavior

No — `create` cannot return `ResponseEntity.ok(...)` from the service. That is a web type; returning it leaks the controller layer into the service. The service returns a `Customer` (or throws); the controller wraps the result in a `ResponseEntity`.

## Scope

Pre-lab only.
