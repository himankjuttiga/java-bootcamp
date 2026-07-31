# Lab 15 — Interface and Constructor Sketch

## Step 1 — Interface

`CustomerService` methods:

- `Customer findById(String customerId)` — fetch one customer, throw NotFound if absent.
- `Customer activate(String customerId)` — apply PROSPECT -> ACTIVE, throw on invalid transition.
- (supporting) `Customer createCustomer(...)`, `Customer updateStatus(String, CustomerStatus)`.

## Step 2 — Constructor

Plain JDK-style constructor injection (no framework):

public CustomerService(CustomerRepository repository, CustomerNotifier notifier) {
this.repository = repository; // required dependency
this.notifier = notifier; // optional side-effect dependency
}


Dependencies: `CustomerRepository` (required, persistence) and an optional `CustomerNotifier`
(fired on successful activate).

## Step 3 — No framework magic

Prefer explicit constructor injection over field injection: dependencies are final, visible,
and testable (pass a fake repository/notifier in unit tests), with no reflection or hidden
container wiring.

## Step 4 — Prep boundary

*Prepare for Lab 15; do not complete full service implementation now.*

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.