# Lab 26 — Profile Purposes

| Profile | Purpose |
| --- | --- |
| dev | Local CRM smoke and demos; relaxed DEBUG logging; H2-friendly in-memory database; developer conveniences (H2 console) enabled. Fixtures Amina (CUS-1001 / ACTIVE) and Ravi (CUS-1002 / PROSPECT) must stay callable. |
| test | Isolated, deterministic automated tests (Surefire / @SpringBootTest); fresh in-memory H2 created per run (create-drop); external services mocked; quiet logging for fast, repeatable runs. |
| prod | Deployed live settings; strict security; real HA database; secrets supplied only via environment variables; minimal logging; fails fast at startup if required properties are missing. |

## One risk if prod uses dev YAML

Production would boot with dev verbosity and insecure defaults — an open H2 console, verbose SQL logging, and a blank/hard-coded DB password committed in YAML. That leaks credentials into source control and exposes the live database to unauthorized access, instead of failing fast and pulling secrets from the environment.

## Scope

Pre-lab only.
