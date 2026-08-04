# Lab 19 — Regression Notes (Northstar CRM)

## Test scope (the pyramid)
| Layer | Class | What it proves |
| ----- | ----- | -------------- |
| Unit (base, Labs 17–18) | service/validator tests | Business rules in isolation with mocks |
| Integration (middle) | `CustomerApiIT` | Real HTTP boundary: create (201 + `X-Correlation-Id` echo), get (200), not-found (404) |
| UI (top) | `CustomerUiIT` + `CustomerFormPage` | Browser create journey through the real controller |

The API IT is cheap and fast, so it runs as the first regression gate before the
slower, Chrome-dependent UI suite.

## Fixtures
Seeded in `InMemoryCustomerRepository`: Amina `CUS-1001` (ACTIVE), Ravi `CUS-1002`
(PROSPECT). UI create uses `CUS-2001`; API create uses `CUS-3001`; `CUS-9999` is
the not-found id. Correlation header value: `lab-request-001`.

## Locators and Page Object
The UI is located strictly by `data-testid` (`customer-id`, `full-name`, `email`,
`status`, `submit-customer`, `create-result`) — never by brittle nth-child CSS or
absolute XPath. All locators live in `CustomerFormPage`; tests read like a script
and assertions stay in the test, not the page object.

## Waits (no blind sleeps)
`WebDriverWait` (10s) waits for a specific condition — the form field visible on
open, and the result region non-empty after submit. No `Thread.sleep`. This
synchronizes on the actual UI state instead of racing a fixed clock.

## CI browser strategy
`WebDriverManager.chromedriver().setup()` resolves a ChromeDriver matching the
installed Chrome, so CI agents match local laptops. Chrome runs `--headless=new`
with `--no-sandbox`/`--disable-dev-shm-usage` for containerized CI. No
ChromeDriver binary is committed. `driver.quit()` in `@AfterEach` prevents orphan
processes.

## Deliberate-failure evidence
Break a `data-testid` (or point `baseUrl` at a stopped app), watch the UI test go
red, capture a screenshot via `TakesScreenshot`, then restore the locator and
confirm green. This proves the regression suite actually fails when the contract
breaks.

## Correlation forward-link
`X-Correlation-Id: lab-request-001` is attached on create and echoed back. Lab 20
keys structured logs off this same header and the same fixture ids, so evidence
from API, UI, and logs can be joined.

## Hygiene
`target/`, drivers, and screenshots stay out of Git; fictional emails only.
