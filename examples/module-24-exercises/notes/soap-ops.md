# Lab 24 — SOAP Operation Map

| SOAP operation | CustomerService method |
| --- | --- |
| GetCustomer | getById / get(id) |
| CreateCustomer | create |
| UpdateCustomer | update |
| DeleteCustomer | delete |

## Shared service?

Yes — one `CustomerService` bean backs both REST and SOAP. The SOAP `@Endpoint` stays thin: it maps XML request/response types and delegates to the same service, so both protocols read and write one store.

## Debug / design challenge

If SOAP uses a second `InMemoryCustomerRepository`, you get split-brain data: a customer created over REST is invisible over SOAP and vice versa. Amina and Ravi would exist in one store but not the other, producing inconsistent reads depending on protocol. One shared service/repository avoids this.

## Predict the output / behavior

No — `GetCustomer` should not re-validate business rules the service already enforces. The endpoint maps and delegates; validation and business rules live once in `CustomerService`. Duplicating them in the endpoint risks the two copies drifting apart.

## Scope

Pre-lab only.
