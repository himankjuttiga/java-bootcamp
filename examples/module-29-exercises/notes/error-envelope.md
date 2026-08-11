# Lab 29 — ErrorResponse Envelope

## Fields

`status` (int, e.g. 400), `code` (stable string, e.g. `VALIDATION_FAILED`), `message` (human-readable, safe), `correlationId` (echoes `lab-request-001`), `timestamp` (Instant), `violations[]` (field errors, empty for non-validation errors).

## Violation item shape

`{ "field": "email", "message": "must be a well-formed email address" }`. One entry per rejected field. Do **not** include `rejectedValue` for sensitive fields.

## Correlation rule

Always echo the incoming `X-Correlation-Id` (e.g. `lab-request-001`) into `correlationId` when provided; generate/leave a placeholder if absent. This ties a client-visible error to server logs.

## Answers to the prompts

- **Rejected passwords in `violations.rejectedValue`:** no — never echo secrets or passwords back in the envelope. Omit `rejectedValue` for sensitive fields entirely.
- **Is a plain string body enough for Pass?** No — Lab 29 requires the structured `ErrorResponse` envelope (status, code, message, correlationId, violations), not just a string, so every client renders one consistent error component.

## Scope

Pre-lab only.
