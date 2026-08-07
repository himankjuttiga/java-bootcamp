# Lab 26 — ConfigurationProperties Sketch

## Class name

`NorthstarIntegrationProperties` (annotated `@ConfigurationProperties` + `@Validated`).

## Prefix

`northstar.integration`

Binds YAML like:

```yaml
northstar:
  integration:
    api-base-url: ${NORTHSTAR_API_BASE_URL}
    api-key: ${NORTHSTAR_API_KEY}      # env-only in prod
    connect-timeout-ms: ${NORTHSTAR_TIMEOUT_MS:2000}
```

## Fields

- `apiBaseUrl` (String) — `@NotBlank`, no hard-coded default.
- `apiKey` (String) — `@NotBlank`, supplied from environment in prod, never in code.
- `connectTimeoutMs` (int/long) — relaxed-binding maps `connect-timeout-ms`; may carry a safe non-secret default.

Relaxed binding: `api-base-url`, `API_BASE_URL`, and `apiBaseUrl` all map to the same field.

## How enabled

`@EnableConfigurationProperties(NorthstarIntegrationProperties.class)` on a config class, or `@ConfigurationPropertiesScan` on the application class.

## Why typed over scattered @Value

One cohesive, testable bean groups related settings, gives type safety, supports `@Validated` fail-fast at startup, and documents the contract in one place — instead of five unrelated `@Value` fields that each fail silently or ship a null (e.g. a null API base URL).

## If prefix mismatches YAML

The fields bind to nothing and stay null / default. Validation (`@NotBlank`) then fails fast at startup; without validation the app boots with null values and breaks later at first integration call.

## Scope

Pre-lab only.
