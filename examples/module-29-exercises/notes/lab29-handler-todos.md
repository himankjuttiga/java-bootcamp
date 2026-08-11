# Lab 29 — GlobalExceptionHandler TODOs

## Advice annotation

`@RestControllerAdvice class GlobalExceptionHandler` — component-scanned (under `com.northstar.crm`) so it applies across all controllers.

## Handlers (list)

- `handleMethodArgumentNotValid(MethodArgumentNotValidException)` -> 400, code `VALIDATION_FAILED`, populates `violations[]`.
- `handleNotFound(CustomerNotFoundException)` -> 404, code `CUSTOMER_NOT_FOUND`.
- `handleDuplicate(DuplicateCustomerException)` -> 409, code `DUPLICATE_CUSTOMER`.
- `handleIllegalTransition(IllegalStateTransitionException)` -> 400/422, code `ILLEGAL_TRANSITION`.
- `handleGeneric(Exception)` -> 500, code `INTERNAL_ERROR`, generic message only.

## 500 rule

The generic 500 handler returns a safe envelope with a generic message and correlationId only. Never leak stack traces, SQL, secrets, or internal class names to the client; log the detail server-side instead.

## Answers to the prompts

- **`CustomerNotFoundException` before `Exception`:** yes — specific handlers must be declared/matched before the catch-all `Exception` handler, otherwise the generic 500 would swallow the 404. Spring picks the most specific match, but keep the generic one as the last resort.
- **Where the advice lives:** in a package scanned by the application (under `com.northstar.crm`) so `@RestControllerAdvice` is registered as a bean.

## Scope

Pre-lab only.
