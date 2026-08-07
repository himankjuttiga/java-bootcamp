# Lab 26 — Activation Command Drill

## -D / Maven run (dev)

```bash
mvn -B spring-boot:run -Dspring-boot.run.profiles=dev
# equivalent via arguments form:
mvn -B spring-boot:run -Dspring-boot.run.arguments=--spring.profiles.active=dev
```

## Env activation (Windows PowerShell + macOS/Linux)

```powershell
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE = "dev"
mvn -B spring-boot:run
```

```bash
# macOS / Linux
export SPRING_PROFILES_ACTIVE=dev
mvn -B spring-boot:run
```

## Tests (test profile)

```bash
mvn -B test -Dspring.profiles.active=test
```

## Answers to the prompts

- **Export in one terminal, run Maven in another:** the export only affects the shell it was set in. A separate terminal has no `SPRING_PROFILES_ACTIVE`, so Maven there ignores it and falls back to the default profile.
- **Default Surefire profile for this lab:** `test`, so `mvn test` runs against isolated in-memory H2 with deterministic settings.

## Prod caution

Never run the prod profile without required env vars set — expect fail-fast at startup. Never write real passwords in these notes.

## Scope

Pre-lab only.
