# Lab 12 — Equals vs ==

## Reference

| Check | Use | Why |
| --- | --- | --- |
| status ACTIVE? | Objects.equals / enum | String identity is unsafe |
| same Customer instance? | == | Reference equality only |
| id CUS-1001? | equals | Value equality |
| status null-safe? | Objects.equals(status, "ACTIVE") | Handles null without NPE |

## Step 2 — Bad snippet

```java
if (status == "ACTIVE") { ... } // Fail -- compares String identity, not value
```

## Step 3 — Good snippet

```java
// Good -- Amina, CUS-1001, status ACTIVE
if (Objects.equals(status, "ACTIVE")) { ... }
// or, with a closed status set:
if (customerStatus == CustomerStatus.ACTIVE) { ... } // enum identity is safe
```

## Step 4 — JDK note

On JDK 21 sketches, prefer an `enum` (e.g. `CustomerStatus { ACTIVE, PROSPECT }`) when the status set is closed. Enum constants are singletons, so `==` is both safe and null-tolerant, and the compiler rejects invalid values.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.

## Fixtures (self-check)

| Customer | ID | Status |
| --- | --- | --- |
| Amina | CUS-1001 | ACTIVE |
| Ravi | CUS-1002 | PROSPECT |

Correlation ID: `lab-request-001`