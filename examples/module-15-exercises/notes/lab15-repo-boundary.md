# Lab 15 — Repository Boundary

## Step 1 — Repo owns

The repository is pure persistence, no business rules:

- CRUD by id: save / findById / findAll / delete on the Map-keyed store.
- Existence checks: containsKey / return Optional for a given customerId.
- Persistence mapping: storing and retrieving the Customer entity shape.
- No knowledge of status transitions, correlation ids, or notifications.

## Step 2 — Service owns

The service owns all business logic:

- Transition matrix: which status moves are legal (e.g. PROSPECT -> ACTIVE for CUS-1002).
- Notifier calls / side effects triggered by a successful activate.
- Domain exceptions: duplicate id (IllegalStateException), unknown id / invalid
  transition (IllegalArgumentException), with correlation lab-request-001.
- Orchestrating the repository: load, apply rule, save.

## Step 3 — Anti-pattern

Anti-pattern: `repo.activateCustomer(...)` — putting the activate business rule inside the
repository. This hides the transition logic in the persistence layer, so the rule cannot be
tested or reused independently and the service loses ownership of its own domain behavior.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.