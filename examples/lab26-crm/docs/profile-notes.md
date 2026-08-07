# Lab 26 — Profile Notes (Northstar CRM)

Correlation / evidence id: `lab-request-001`

## Property override order (highest → lowest)

1. Command-line arguments (`-Dspring.profiles.active`, `-Dnorthstar.integration.connect-timeout-ms=1234`)
2. Environment variables (`SPRING_PROFILES_ACTIVE`, `NORTHSTAR_INTEGRATION_CONNECT_TIMEOUT_MS`, `DB_PASSWORD`, ...)
3. `application-{profile}.yml` (dev / test / prod)
4. `application.yml` (shared base defaults)
5. `@ConfigurationProperties` / `@Value` defaults baked into code

Higher source wins; once a key is found higher up, lower sources are ignored for that key.

## Profile purposes

- **dev** — local sandbox; H2 in-memory `lab26dev`; `com.northstar.crm` at DEBUG; blank H2 password acceptable locally.
- **test** — CI isolation; H2 in-memory `lab26test`; `connect-timeout-ms: 100`; used by `ProfileBindingTest` via `@ActiveProfiles("test")`.
- **prod** — PostgreSQL; `${DB_USERNAME}` / `${DB_PASSWORD}` / `${NORTHSTAR_API_KEY}` env-only, no defaults; missing secrets fail startup fast.

## Activation evidence (two ways)

### 1. `-D` / Maven run (dev)

```bash
mvn -B spring-boot:run -Dspring-boot.run.profiles=dev
# banner: The following 1 profile is active: "dev"
```

Packaged form after `mvn package`:

```bash
java -Dspring.profiles.active=dev -jar target/lab26-crm-0.0.1-SNAPSHOT.jar
```

### 2. Environment variable (test)

```bash
# macOS / Linux
export SPRING_PROFILES_ACTIVE=test
mvn -B spring-boot:run
unset SPRING_PROFILES_ACTIVE
```

```powershell
# Windows PowerShell
$env:SPRING_PROFILES_ACTIVE = "test"
mvn -B spring-boot:run
Remove-Item Env:SPRING_PROFILES_ACTIVE
```

## Override-order measurement — `connect-timeout-ms` (full path)

| Layer | Source | Effective connect-timeout-ms |
| ----- | ------ | ---------------------------- |
| Profile YAML | `application-test.yml` | 100 |
| Env var | `NORTHSTAR_INTEGRATION_CONNECT_TIMEOUT_MS=9999` | 9999 |
| CLI `-D` | `-Dnorthstar.integration.connect-timeout-ms=1234` | 1234 |

CLI beats env; env beats profile YAML. Read back via `NorthstarIntegrationProperties.getConnectTimeoutMs()`.

## dev CRM smoke

```bash
curl -s -H "X-Correlation-Id: lab-request-001" http://localhost:8080/api/customers/CUS-1001
# {"id":"CUS-1001","name":"Amina Khan","email":"amina.khan@example.com","status":"ACTIVE"}
```

## prod fail-fast

```bash
mvn -B spring-boot:run -Dspring-boot.run.profiles=prod   # no env vars set
# APPLICATION FAILED TO START — Could not resolve placeholder 'DB_PASSWORD' / 'NORTHSTAR_API_KEY'
```

No default (`${DB_PASSWORD:}`) is used, so the app refuses to connect with a blank password.

## Secrets hygiene

- `.env.example` committed (placeholders only); `.env` is gitignored.
- `git status --short` shows no `.env`, no real passwords, no `target/`.

## Failure experiments

| # | Experiment | Observed | Restore |
| - | ---------- | -------- | ------- |
| 1 | prod without `DB_PASSWORD` | Fail-fast startup, unresolved placeholder | Unset experiment vars |
| 2 | env `test` + CLI `dev` together | CLI `dev` wins | Unset env |
| 3 | rename key only in YAML (binding mismatch) | Falls back to base default / null | Fix key name |
