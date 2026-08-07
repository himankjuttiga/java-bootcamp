# Lab 26 — Property Override Order

## Highest to lowest

1. Command-line arguments (`--key=value`, `-Dspring-boot.run.arguments=...`)
2. Environment variables (`SPRING_PROFILES_ACTIVE`, `NORTHSTAR_INTEGRATION_TIMEOUT_MS`, etc.)
3. `application-{profile}.yml` (profile-specific: dev / test / prod)
4. `application.yml` (base, shared defaults)
5. Code defaults (`@Value("${key:default}")` / `setDefaultProperties`)

Higher source wins; once a property is found higher up, all lower sources are ignored for that key.

## Property you will measure in lab

`northstar.integration.timeout-ms` — set it in base `application.yml`, override in a profile YAML, override again via an environment variable, then override once more with a CLI arg. Read back the effective value at each step to prove the ranking (YAML → env → CLI). Anchor evidence with correlation ID `lab26-001`.

## Answers to the prompts

- **env INFO vs profile YAML DEBUG:** the environment variable wins (rank 2 beats rank 3), so `logging.level.root` resolves to INFO.
- **Where do `@Value` code defaults sit:** below `application.yml` — they are the lowest precedence and only apply when nothing else supplies the property.

## Scope

Pre-lab only.
