# Lab 12 — SOLID Apply vs Defer

## Step 1 — Apply now

Apply SRP (Single Responsibility Principle): separate the validation helper from the persistence-shaped code in the `CustomerService` sketch, so `validateStatus` lives apart from anything that touches storage.

## Step 2 — Defer

- Defer DIP (Dependency Inversion) wiring through frameworks until Labs 13+.
- Defer ISP (Interface Segregation) for the large SOAP ports until Labs 13+.

## Step 3 — Why defer

Modules 10 through 12 stay before the SOAP and Spring labs, so wiring frameworks and segregating large ports now would over-architect boundaries that do not yet exist.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.

## Fixtures (self-check)

| Customer | ID | Status |
| --- | --- | --- |
| Amina | CUS-1001 | ACTIVE |
| Ravi | CUS-1002 | PROSPECT |

Correlation ID: `lab-request-001`