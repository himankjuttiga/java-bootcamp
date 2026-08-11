# Lab 28 — SecurityFilterChain Sketch

## Session policy

`STATELESS` — no server-side session; every request authenticates from its Bearer JWT. CSRF disabled (safe for a pure Bearer-token API with no session cookie).

## Login matcher

`/api/auth/login` -> `permitAll` (must be open so users can obtain a token). Actuator health may also be `permitAll` if needed.

## Customers matcher + roles

`/api/customers/**` -> `hasAnyRole("AGENT", "ADMIN")` — both roles may read customers.

## Admin matcher + roles

`/api/admin/**` -> `hasRole("ADMIN")` — ADMIN only.

## Default rule and filter order

`anyRequest().authenticated()` last, so any new controller is protected by default (default deny, not open-by-default). The JWT authentication filter runs **before** `UsernamePasswordAuthenticationFilter` so the token is validated and the `SecurityContext` populated before authorization checks.

## Answers to the prompts

- **CSRF for a pure Bearer JWT API:** disable it. CSRF protects cookie/session browser flows; a stateless Bearer API has no ambient session cookie to forge, so CSRF adds no protection here.
- **If `/api/customers/**` were `permitAll`:** anyone, including anonymous callers with no token, could read customer data, leaking PII and defeating the whole security layer.

## Scope

Pre-lab only.
