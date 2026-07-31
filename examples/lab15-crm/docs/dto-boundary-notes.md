# DTO Boundary Notes — Lab 14

## Entity vs DTO
The `Customer` entity is the persistence/domain shape. `CustomerRequestDTO` and
`CustomerResponseDTO` are the API contract shapes. Keeping them separate means storage
changes do not silently reshape the public contract, and internal fields never leak.

## Trust boundary
Validation is the first gate. `CustomerApiFacade.create` runs Bean Validation on the request
DTO before any service call, so blank/invalid payloads never reach `CustomerService`. The
service's own rules (duplicate id → IllegalStateException) are a second, separate gate.

## Why no entity on the wire
Returning `Customer` would expose `phone`, internal timestamps, `equals/hashCode`, and future
persistence annotations to callers. `CustomerMapper.toResponse` returns only id, name, email,
status, and createdAt.

## Correlation
`lab-request-001` is echoed in every validation and not-found failure so support can trace a
request end to end.