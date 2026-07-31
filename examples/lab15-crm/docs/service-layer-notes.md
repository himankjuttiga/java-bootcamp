markdown
# Service Layer Notes — Lab 15

## Two kinds of validation
- Bean Validation (Lab 14, CustomerRequestDTO): checks SHAPE — not-blank, email format, sizes.
- CustomerValidator (this lab): checks MEANING — duplicate id/email and legal status transitions.

## Allowed status transitions

PROSPECT -> ACTIVE, CLOSED
ACTIVE -> SUSPENDED, CLOSED
SUSPENDED -> ACTIVE, CLOSED
CLOSED -> (none)

Same-status changes (e.g. ACTIVE -> ACTIVE) are rejected as illegal transitions, not silent no-ops.

## Layering / no leak
- CustomerRepository is the persistence PORT; InMemoryCustomerRepository is the adapter.
- The Map lives ONLY inside InMemoryCustomerRepository (private). No HashMap/SQL/EntityManager
  in the service package. listAll() returns List.copyOf(...) so callers cannot mutate storage.

## Manual wiring (Spring DI preview)
```java
CustomerRepository repo = new InMemoryCustomerRepository();
CustomerValidator validator = new CustomerValidator(repo);   // same repo instance
CustomerService service = new DefaultCustomerService(repo, validator);
```
Under Spring later these become @Repository / @Service constructor-injected beans; the rules do not change.

## Order matters
changeStatus validates the transition BEFORE calling setStatus, so a rejected transition
leaves the stored status unchanged (proven by DefaultCustomerServiceTest).