# Lab 19 — data-testid Locators

| Element | data-testid |
| --- | --- |
| Status | customer-status |
| Activate/submit | activate-customer / submit-customer |
| Customer id/name | customer-id / customer-name |

## Brittle alternative
`div.col-md-3 > span:nth-child(2)` and absolute XPath like `/html/body/div[3]/button` — both break the moment markup or CSS classes are reordered or renamed.

## Contract note
A data-testid is an explicit HTML contract for automation: keep the testids stable across UI polish and redesigns, even when styling classes change. Marketing can rename CSS freely without breaking the Selenium suite.

## Scope
Pre-lab only.