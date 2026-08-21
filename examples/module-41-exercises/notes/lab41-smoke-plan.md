# Smoke plan

1. `docker build -t crm-api:lab41 .`
2. `docker run --env-file .env -p 8080:8080 crm-api:lab41`
3. curl readiness `/actuator/health/readiness` (expect UP)
4. create/get `CUS-1001` with header `X-Correlation-Id: lab-request-001`
5. `docker stop` (graceful SIGTERM)

Negative: wrong `SPRING_DATASOURCE_URL` → readiness stays down.
Scope: plan only, the full docker build/run is Lab 41.

**Self-mark:** Pass
