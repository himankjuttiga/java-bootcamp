# Lab 35 — Endpoint Map

## Reference

| UI action | HTTP |
| --- | --- |
| List customers | GET /api/customers |
| Open Amina | GET /api/customers/CUS-1001 |
| Create customer | POST /api/customers |
| Update status | PATCH /api/customers/{id} |
| Replace customer | PUT /api/customers/{id} |

## Step 2 — Ravi row

| UI action | HTTP |
| --- | --- |
| Open Ravi | GET /api/customers/CUS-1002 |

The browser only speaks JSON over HTTP to Spring. It never calls Kafka topics or SOAP endpoints directly; those stay server-side behind `/api`.

## Step 3 — Status codes

| Code | When the UI sees it | UI reaction |
| --- | --- | --- |
| 200 | GET list or GET one succeeded | render data, or empty state when the array is `[]` |
| 201 | POST created a customer | close the form, refresh the list |
| 400 | validation failed (blank name, bad status) | show a field-level message from `detail` |
| 404 | unknown id such as `CUS-9999` | show a "not found" panel, not a crash |
| 500 | server fault | show a safe retry message, log the correlation id |

## Step 4 — JSON shape

List item JSON:

```json
{ "customerId": "CUS-1001", "name": "Amina Khan", "status": "ACTIVE" }
```

Full list response:

```json
[
  { "customerId": "CUS-1001", "name": "Amina Khan", "status": "ACTIVE" },
  { "customerId": "CUS-1002", "name": "Ravi Singh", "status": "PROSPECT" }
]
```

Base URL trap: if `VITE_CRM_API_URL` already ends in `/api`, appending `/api/customers` gives `/api/api/customers`, which returns 404 even though the backend is healthy. Keep the base as `http://localhost:8080` and the path as `/api/customers`.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
