# Lab 23 — Auto-Config vs Ownership Notes

## What Spring Boot auto-configures (gifts)

1. Embedded Tomcat + `DispatcherServlet` (from `spring-boot-starter-web`).
2. Jackson JSON serialization/deserialization for request and response bodies.
3. Actuator health/info endpoint infrastructure (from `spring-boot-starter-actuator`).

## What the team still owns

1. Domain rules — customer create/get behavior and the missing-ID failure (Boot default 500 unless we add `@ControllerAdvice`).
2. Fixtures and seed data — `CUS-1001` (Amina, ACTIVE) and `CUS-1002` (Ravi, PROSPECT).
3. Exposure and secrets policy — which Actuator endpoints are exposed, and (Lab 26) how real secrets are externalized. No secrets committed here.

## One-sentence rule

Boot auto-configures the plumbing from the classpath, but never our business validation, fixtures, or exposure policy.

## Profile teaser

`application-dev.yml` raises logging to DEBUG; `application-prod.yml` drops to WARN and tightens Actuator (`show-details: never`, `health` only). Real profile/secrets discipline is Lab 26. Unrestricted Actuator here is lab-only.
