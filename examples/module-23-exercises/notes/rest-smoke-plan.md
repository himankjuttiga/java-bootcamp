# Lab 23 — REST Smoke Plan

## Start command

`mvn spring-boot:run` (from `examples/lab23-crm`), wait for `Started CrmApplication`.

## Health check

`GET http://localhost:8080/actuator/health` → expect `{"status":"UP"}` before grading anything else.

## CUS-1001 steps

1. POST `/api/customers` with Amina, ACTIVE, header `X-Correlation-Id: lab-request-001` → expect 201.
2. GET `/api/customers/CUS-1001` → expect 200, Amina, status ACTIVE.

## CUS-1002 steps

3. POST `/api/customers` with Ravi, PROSPECT, header `X-Correlation-Id: lab-request-001` → expect 201.
4. GET `/api/customers/CUS-1002` → expect 200, Ravi, status PROSPECT.

## Correlation header/id

`X-Correlation-Id: lab-request-001` on every request; capture screenshots under `notes/screenshots/lab-23/`.

## Debug / design challenge

No — if health is DOWN you should not grade the REST steps as Pass. Health DOWN means the app or a dependency isn't ready, so any REST result is unreliable; fix health first, then re-run the smoke.

## Predict the output / behavior

Screenshots go under `notes/screenshots/lab-23/`.

## Scope

Pre-lab only.
