# lab23-crm — Northstar CRM First Boot App

Spring Boot 3.3.5 CRM slice: REST `/api/customers`, Actuator health, embedded Tomcat on port 8080.

## Run

```bash
mvn spring-boot:run
# dev profile teaser:
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Smoke

```bash
curl -s http://localhost:8080/actuator/health          # {"status":"UP"}
curl -s http://localhost:8080/actuator/info

# create Amina + Ravi (correlation header read, default lab-request-001)
curl -H "X-Correlation-Id: lab-request-001" -H "Content-Type: application/json" \
  -d "{\"id\":\"CUS-1001\",\"name\":\"Amina Khan\",\"email\":\"amina.khan@example.com\",\"status\":\"ACTIVE\"}" \
  http://localhost:8080/api/customers
curl -H "X-Correlation-Id: lab-request-001" -H "Content-Type: application/json" \
  -d "{\"id\":\"CUS-1002\",\"name\":\"Ravi Singh\",\"email\":\"ravi.singh@example.com\",\"status\":\"PROSPECT\"}" \
  http://localhost:8080/api/customers

curl -s http://localhost:8080/api/customers/CUS-1001
curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/customers/CUS-MISSING   # 500 (Boot default)
```

## Test

```bash
mvn -B -Dtest=CrmApplicationTests test    # Tests run: 1
mvn -B clean verify
```

## Notes

- Missing-ID returns HTTP 500 (unhandled `IllegalArgumentException`); adding `@ControllerAdvice` to return 404 is optional full-path work.
- Actuator exposure here (`health,info`) is lab-only; production hardening and real secrets are Lab 26.
- See `docs/autoconfig-notes.md` for the auto-config vs ownership breakdown.

## Cleanup

```bash
# Ctrl+C to stop spring-boot:run
mvn -q clean
```
