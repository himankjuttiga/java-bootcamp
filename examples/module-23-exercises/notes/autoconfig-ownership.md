# Lab 23 — Auto-Config Versus Ownership

| Boot / auto-config gift | Still owned by the team |
| --- | --- |
| Embedded Tomcat + DispatcherServlet | Customer create/get business rules (e.g. duplicate CUS-1001 handling) |
| Jackson JSON serialization/deserialization | Fixture IDs and seed data (CUS-1001 / CUS-1002) |
| Actuator health endpoint infrastructure | Which endpoints to expose and how they are secured |

## One-sentence rule

Boot auto-configures the plumbing (server, MVC, JSON, health wiring) from what is on the classpath, but it never invents your business validation, fixtures, or exposure policy — those stay team-owned.

## Debug / design challenge

Health UP but POST create always returns 500 is an **ownership** problem, not an auto-config failure. Health UP proves Boot wired the server and context correctly; a consistent 500 on create means our own service/validation logic is throwing, which is code we own.

## Predict the output / behavior

Removing `spring-boot-starter-web` would take away the embedded Tomcat, Spring MVC, and Jackson auto-configuration — so the REST endpoints `/api/customers` would no longer start or serve, and the app would not run as a web server at all.

## Scope

Pre-lab only.
