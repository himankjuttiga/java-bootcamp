# Operation Matrix — CustomerService (Lab 13)

Namespace: `http://northstar.com/crm/customer`
Style: document/literal · Placeholder endpoint: `http://localhost:8080/ws` (not live; hosted in Lab 24)

| Operation | Purpose | Key inputs | Key outputs | Fault |
| --------- | ------- | ---------- | ----------- | ----- |
| CreateCustomer | Register a new CRM customer | fullName, email, phone?, status?, correlationId? | customer (with assigned ID) | validation (blank field) |
| UpdateCustomer | Change mutable fields / status | customerId, optional fields, correlationId? | customer | not-found, validation |
| GetCustomer | Fetch one customer by ID | customerId, correlationId? | customer | not-found (e.g. CUS-9999) |

Out of scope for Lab 13 (documented as future): DeleteCustomer, ListCustomers.

## Fixtures
| ID | Name | Status | Email |
| -- | ---- | ------ | ----- |
| CUS-1001 | Amina Khan | ACTIVE | amina.khan@example.com |
| CUS-1002 | Ravi Singh | PROSPECT | ravi.singh@example.com |

Correlation ID: `lab-request-001`