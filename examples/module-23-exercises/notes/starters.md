# Lab 23 — Boot Starters Inventory

| Starter | Why for CRM lab |
| --- | --- |
| spring-boot-starter-web | REST `/api/customers` endpoints, Spring MVC, embedded Tomcat, and Jackson JSON |
| spring-boot-starter-actuator | `/actuator/health` smoke check to prove the app is up |
| spring-boot-starter-test | Spring Test + JUnit 5 + Mockito for ContextLoads and API integration tests |
| spring-boot-starter-validation (optional) | `@Valid` request bodies for bean validation on create payloads |

## Debug / design challenge

No — `starter-actuator` does not replace `starter-web`. Actuator provides operational endpoints (health, info) but depends on the web layer to serve them over HTTP; without `starter-web` there is no embedded server or MVC, so neither the REST API nor the Actuator HTTP endpoints work.

## Predict the output / behavior

Without `spring-boot-starter-test`, `mvn test` breaks at compile time — the test classes cannot resolve `@SpringBootTest`, JUnit 5, `MockMvc`/`TestRestTemplate`, or assertions, so the module fails to compile the test sources.

## Scope

Pre-lab only.
