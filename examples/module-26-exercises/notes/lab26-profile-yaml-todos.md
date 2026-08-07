# Lab 26 — Profile YAML TODOs

## Required files

- `application.yml` — base, shared defaults loaded in every environment.
- `application-dev.yml` — development overrides.
- `application-test.yml` — test overrides.
- `application-prod.yml` — production overrides (no secret defaults).

Filenames must use the `application-{profile}.yml` form. `application.dev.yml` (dot instead of hyphen) never loads as a profile.

## Base keys

- `spring.application.name: northstar-crm`
- `server.port: 8080`
- `management` / actuator defaults
- Shared `logging` pattern
- `northstar.integration.timeout-ms` (default that dev/env/CLI can override)

## dev example key

```yaml
logging:
  level:
    com.northstar: DEBUG
spring:
  datasource:
    url: jdbc:h2:mem:devdb;DB_CLOSE_DELAY=-1
  h2:
    console:
      enabled: true
```

## prod secret pattern

```yaml
spring:
  datasource:
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}   # env-only, NO default
northstar:
  integration:
    api-key: ${NORTHSTAR_API_KEY}
```

Reference secrets as `${ENV_VAR}` with no default so startup fails fast when they are missing. An empty default such as `${DB_PASSWORD:}` is **not** acceptable in prod — it lets the app connect with a blank password, which is exactly the incident this lab prevents.

## Scope

Pre-lab only. No real passwords.
