# Lab 19 — Page Object Sketch

## Class name
CustomerFormPage (and CustomerStatusPage), each holding a WebDriver field.

## Actions
open(), fillName(String name), fillCustomerId(String id), submit() / clickActivate().
These locate elements by data-testid (customer-name, customer-id, submit-customer, activate-customer) and perform the interaction only.

## Queries
readStatus() returns the customer-status text (e.g. "ACTIVE"); readCustomerId() returns customer-id text. Queries return data, they do not assert.

## Asserts live in
The tests, not the page object. The test calls page.readStatus() and does assertEquals("ACTIVE", ...). The page object stays a thin, reusable wrapper.

## Scope
Pre-lab only.