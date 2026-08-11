# Lab 29 — DTO Constraint Plan

| Field | Constraints |
| --- | --- |
| fullName | `@NotBlank`, `@Size(min = 2, max = 100)` |
| email | `@NotBlank`, `@Email` |
| status | `@NotNull` (allowed values `ACTIVE` / `PROSPECT`, enforced by enum or a service check) |

## How triggered

Constraints live on the request DTO `CustomerRequest`, not only the entity. They fire when the controller create method parameter is annotated `@Valid` (e.g. `create(@Valid @RequestBody CustomerRequest req)`). A violation raises `MethodArgumentNotValidException`, which the global handler turns into a 400.

## Answers to the prompts

- **Annotations present but `@Valid` missing:** nothing is validated — the invalid body binds and flows through, so a blank name or bad email is accepted and fails later (or corrupts data). `@Valid` is what triggers the checks.
- **Uniqueness of `CUS-1001`:** a service rule returning 409, not a Bean Validation annotation. Bean Validation checks a single request in isolation; uniqueness needs a store lookup, so it belongs in the service (`DUPLICATE_CUSTOMER` / 409).

Fixtures: Amina `CUS-1001`/ACTIVE, Ravi `CUS-1002`/PROSPECT, not-found `CUS-9999`, correlation `lab-request-001`.

## Scope

Pre-lab only.
