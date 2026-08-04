# Lab 20 — Log Level Quiz

| Event | Level |
| --- | --- |
| Activate success Ravi | INFO |
| Illegal transition Amina | WARN |
| Unexpected repo failure | ERROR |
| Mapper field copy detail | DEBUG |

## Prod habit

DEBUG stays off by default in production profiles; it is enabled only temporarily, scoped to a single class or package, for live troubleshooting.

## Fixtures

Amina `CUS-1001` / `ACTIVE`, Ravi `CUS-1002` / `PROSPECT`, correlation `lab-request-001`. IDs only, never PII.

## Debug / design challenge

Blank-name validation → **WARN**. A blank name is an expected, client-caused input rejection that the service handles by returning a safe validation response. It is not a defect in our code, so it does not warrant an ERROR stack trace. Reserve ERROR for unexpected failures that need investigation (e.g. the repository failure above). If blank names spike, that is better surfaced through metrics than by paging on-call for every occurrence.

## Predict the output / behavior

If root is DEBUG in production, the first operational problem is log-volume flooding: every library and framework emits its DEBUG detail, which overwhelms storage and I/O, drives up cost, and buries the meaningful INFO/WARN/ERROR events so real incidents become hard to find.

## Scope

Pre-lab only.