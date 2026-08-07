# Lab 25 — Service Test Plan

| Case | Setup | Expect |
| --- | --- | --- |
| get CUS-1001 | fresh in-memory repo seeded with Amina | returns Amina, status ACTIVE |
| duplicate create | repo already has CUS-1001 | conflict/duplicate exception thrown |
| get CUS-9999 | repo without that id | not-found exception thrown |
| create new (CUS-3001) | fresh repo | customer saved and returned |

## Spring Boot required for unit test?

No — construct the service directly with a fresh `InMemoryCustomerRepository` (or a fake) and call its methods. No `@SpringBootTest`, no Tomcat, no MockMvc for pure service unit tests.

## Debug / design challenge

Prefer a fresh repository per `@BeforeEach` so each test starts from a known, isolated state. Shared mutable map state across tests causes order-dependence and flaky failures (a create in one test leaking into another).

## Predict the output / behavior

No — these unit tests should not call `CustomerController`. They test the service in isolation; exercising the controller belongs to a separate web-layer test (MockMvc/`@WebMvcTest`).

## Scope

Pre-lab only.
