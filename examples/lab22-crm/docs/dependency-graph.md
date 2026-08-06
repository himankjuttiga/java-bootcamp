# Lab 22 Dependency Graph

CustomerController → CustomerService → CustomerRepository (InMemoryCustomerRepository)
                                   ↘ NotificationService

All default singleton.
Correlation: X-Correlation-Id / lab-request-001
Lab IDs: CUS-1001, CUS-1002
Anti-pattern: new InMemoryCustomerRepository() inside CustomerService

Notes:
- All edges are constructor parameters (constructor injection, final fields).
- Domain type `Customer` is a plain JavaBean — no Spring annotations.
- `CustomerMetrics` is not part of this lab.
