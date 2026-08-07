# Lab 25 — Layer Boundary Quiz

| Responsibility | Layer (Controller / Service / Repository) |
| --- | --- |
| Map JSON ↔ HTTP status | Controller |
| Reject duplicate CUS-1001 | Service |
| Store Customer by id | Repository |
| PROSPECT → ACTIVE rule | Service |
| May import CustomerRepository? | Service (not Controller) |

## Debug / design challenge

A controller that calls `map.put(...)` directly collapses three layers into one. Split it: the controller maps JSON and HTTP status and calls `customerService.create(...)`; `CustomerService` enforces the business rules (duplicate check, status transitions) and calls `customerRepository.save(...)`; `InMemoryCustomerRepository` owns the map. The controller never touches storage.

## Predict the output / behavior

If `ResponseEntity` appears inside `CustomerService`, the **web/controller layer has leaked into the service**. HTTP types belong only in the controller; the service should throw domain exceptions and return domain objects.

## Scope

Pre-lab only.
