# Lab 19 — Northstar CRM Integration + UI Regression Suite

Spring Boot 3.3 web app with an HTTP integration suite (`CustomerApiIT`) and a
Selenium Page Object UI suite (`CustomerUiIT` + `CustomerFormPage`), both keyed to
the `X-Correlation-Id: lab-request-001` contract and `data-testid` locators.

## Run it
```bash
mvn -q -Dtest=CustomerApiIT test        # HTTP integration (fast, no browser)
mvn -q -Dtest=CustomerUiIT test         # Selenium UI (needs Chrome/Chromium)
mvn -q clean verify                     # full suite
mvn spring-boot:run                     # then open http://localhost:8080/customers.html
```
Chrome or Chromium must be installed for the UI suite; WebDriverManager resolves a
matching ChromeDriver automatically.

## Endpoints
`POST /api/customers` (201 + echoed `X-Correlation-Id`) and
`GET /api/customers/{id}` (200 or 404). Seeded ids: Amina `CUS-1001`, Ravi `CUS-1002`.

## Layout
`api/CustomerController`, `service/CustomerService`, `repository/*`, `model/Customer`,
`resources/static/customers.html`; tests under `integration/` and `ui/`. Scope and
CI browser policy: [`docs/regression-notes.md`](docs/regression-notes.md).

## Hygiene
No committed drivers, `target/`, or secrets. UI located by `data-testid`; explicit
waits only, no `Thread.sleep`.
