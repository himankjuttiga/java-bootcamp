# Lab 20 — logging contract

## MDC keys

| Key | Meaning |
| --- | ------- |
| corr | X-Correlation-Id |
| cust | customerId |
| op | create / get |

## Rules

- Never log fullName or email
- Always `MDC.clear()` in filter `finally`
- Levels: INFO success path; WARN business reject; ERROR unexpected

## Sample INFO lines (from smoke test)

```text
2026-08-04T10:15:30.000Z INFO  [http-nio-8080-exec-1] c.n.crm.service.CustomerService corr=lab-request-001 cust=CUS-1001 op=get - Loading customer
2026-08-04T10:15:31.000Z INFO  [http-nio-8080-exec-2] c.n.crm.service.CustomerService corr=lab-request-001 cust=CUS-1002 op=create - Creating customer
2026-08-04T10:15:31.010Z INFO  [http-nio-8080-exec-2] c.n.crm.service.CustomerService corr=lab-request-001 cust=CUS-1002 op=create - Customer created status=PROSPECT
```

Controlled-failure sample (POST with blank fullName):

```text
2026-08-04T10:15:32.000Z WARN  [http-nio-8080-exec-3] c.n.crm.api.CustomerController corr=lab-request-001 cust= op= - Rejecting create reason=missing_full_name cust=CUS-9999
```

No line contains a name, email, or phone — only safe identifiers and reason codes.
