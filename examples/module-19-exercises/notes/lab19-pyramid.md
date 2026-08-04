# Lab 19 — Test Pyramid for CRM

## Base (unit)
Many fast JUnit/Mockito service tests (Labs 17–18): CustomerServiceMockitoTest, CustomerServiceBddMockTest, CustomerValidatorParameterizedTest, and the real-repo CustomerServiceTests. They cover activate (Ravi CUS-1002 PROSPECT -> ACTIVE), illegal transitions on Amina CUS-1001, duplicates, and not-found (CUS-9999).

## Middle (API IT)
Fewer API integration tests: create then get a customer, illegal-transition returns 409, not-found returns 404, and correlationId lab-request-001 is echoed on failures. These exercise the real collaborators wired together, not mocks.

## Top (UI)
Few Selenium journeys: the Amina add-customer form submits successfully, and a status-change flow (activate Ravi) shows the expected result. Only the highest-value end-to-end paths, kept small because UI tests are slow and brittle.

## Scope
Pre-lab only.