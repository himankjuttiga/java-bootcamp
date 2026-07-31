# Lab 14 — Northstar CRM API Contract Boundary (DTOs + Validation)

Adds a request/response DTO layer with Jakarta Bean Validation over the Lab 12
CustomerService. Entities never cross the API edge; the facade validates first, then maps.

## Fixtures
| ID | Name | Status | Email |
| -- | ---- | ------ | ----- |
| CUS-1001 | Amina Khan | ACTIVE | amina.khan@example.com |
| CUS-1002 | Ravi Singh | PROSPECT | ravi.singh@example.com |

Correlation ID: `lab-request-001`

## Validation rules (CustomerRequestDTO)
| Field | Constraints |
| ----- | ----------- |
| customerId | @NotBlank, @Size(max=32) |
| fullName | @NotBlank, @Size(2..100) |
| email | @NotBlank, @Email, @Size(max=254) |
| status | @NotBlank, @Size(max=32) (ACTIVE \| PROSPECT \| SUSPENDED \| CLOSED) |

## Sample invalid (email)
email=not-an-email → IllegalArgumentException at the facade, before the service:
`[lab-request-001] email: email must be a valid address`

## Design decisions
- Entities stay behind CustomerMapper; the API returns CustomerResponseDTO only.
- Response timestamps use LocalDateTime to match the Lab 12 Customer entity (no Instant conversion).
- Validation runs programmatically via ValidatorFactory now; Spring @Valid replaces the trigger in Lab 29+, not the rules.
- Invalid but non-blank status (e.g. "GOLD") passes Bean Validation, then fails at CustomerStatus.valueOf — documented in failure experiments.

## Run
- Tests: Maven panel → lab14-crm → Lifecycle → test  (Tests run: 12)
- Main demo: Maven panel → exec:java, or run Main.java from IntelliJ

## Scope
In-memory store; no Spring MVC, no HTTP/SOAP transport, no persistence. Kept for Lab 15+.