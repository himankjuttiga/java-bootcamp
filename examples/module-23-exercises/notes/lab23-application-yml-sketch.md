# Lab 23 — application.yml Sketch

## Base keys

application name: lab23-crm
server.port: 8080
management exposure: health (Actuator web exposure include: health)

```yaml
spring:
  application:
    name: lab23-crm
server:
  port: 8080
management:
  endpoints:
    web:
      exposure:
        include: health
```

## dev teaser

`logging.level.root: DEBUG` (verbose local troubleshooting; activated via `spring.profiles.active=dev`)

## prod teaser

`logging.level.root: INFO` — no secrets in the file; real credentials come from environment variables / a secrets manager (Lab 26), never hard-coded here.

## Debug / design challenge

No — the prod teaser must never include a hard-coded database password. Secrets belong in environment variables or a secrets manager and are externalized in a later lab; a password committed to YAML is a credential leak.

## Predict the output / behavior

If `exposure.include` omits `health`, the `/actuator/health` endpoint is not exposed over the web, so the smoke check returns 404 and you cannot prove the app is UP through Actuator.

## Scope

Pre-lab only. No real passwords.
