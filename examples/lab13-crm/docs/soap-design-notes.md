# SOAP Design Notes — Lab 13

## Binding style: document/literal
Chose document/literal over rpc/encoded. It is the WS-I interoperable standard, validates
cleanly against the XSD, and is what Spring-WS expects in Lab 24. rpc/encoded is legacy and
poorly interoperable.

## Correlation placement
Correlation id sits as an optional `correlationId` element in each request body (value
`lab-request-001`). This keeps it visible in samples without requiring WS-Addressing yet.
A bonus option is a SOAP header element; deferred to keep Lab 13 simple.

## Fault design
- NotFound → `soapenv:Client` (the caller asked for an id that does not exist, e.g. CUS-9999).
- Validation → `soapenv:Client` (caller sent a blank/invalid field).
- Both fault strings embed the correlation id so support can trace a request.
- Server-side/unexpected errors would map to `soapenv:Server` (not needed for these samples).

## Retry / idempotency
- GetCustomer: safe to retry (read-only, no state change).
- UpdateCustomer: effectively idempotent — setting status to ACTIVE twice yields the same state.
- CreateCustomer: NOT safely retryable — a blind retry risks a duplicate; the service rejects
  duplicate ids (maps to the Lab 12 `IllegalStateException` behavior).

## Cross-walk to Lab 12
- CreateCustomer ↔ `CustomerService.createCustomer(...)`
- GetCustomer ↔ `getCustomer(customerId)` (throws on unknown)
- UpdateCustomer (status) ↔ `updateStatus(customerId, newStatus)`

## Security deferrals (future)
- No WS-Security in Lab 13. Later: authentication/authorization at a gateway or via
  WS-Security headers; schema validation is the first trust boundary, service rules the second.

## Forward link
Lab 24 implements Spring-WS `@Endpoint` methods against this exact contract and namespace.
Do not add MessageDispatcherServlet or JAXB generation in Lab 13.