# Lab 29 — MockMvc Body Assertions Plan

| Case | Status | Body asserts |
| --- | --- | --- |
| Bad email | 400 | `code == VALIDATION_FAILED`; `violations` not empty (contains `email`); `correlationId == lab-request-001` |
| CUS-9999 | 404 | `code == CUSTOMER_NOT_FOUND`; `message` present; `correlationId` echoed |
| Duplicate CUS-1001 | 409 | `code == DUPLICATE_CUSTOMER`; `correlationId` echoed |
| GET CUS-1001 | 200 | happy path: `id == CUS-1001`, `name == Amina Khan` (not an error envelope) |

## Answers to the prompts

- **Why sort violations / loosen order asserts:** field-error order from Bean Validation is not guaranteed, so either sort violations in the handler for deterministic output or assert membership (`violations[*].field` contains `email`) rather than a fixed index. Prevents flaky tests.
- **Should Lab 28's 401 be tested here?** Keep it separate — Lab 29 tests validation/error-envelope behavior; the 401 security path belongs to `SecurityPathTest` from Lab 28. Mixing concerns makes failures harder to localize.

## Scope

Pre-lab only.
