# Lab 21 — Cardinality Anti-Patterns

| Label | OK? |
| --- | --- |
| outcome=success\|failure | yes — ~2 fixed values, bounded |
| customerId=CUS-1001 | no — one new series per customer, forever |
| correlationId=lab-request-001 | no — unbounded per request; use logs |

## Where ids go

Customer IDs, correlation IDs, and trace IDs belong in logs and traces, never in metric labels. Metrics stay aggregate and low-cardinality; you pivot from a metric alert to the specific request by correlation/trace ID in the logs.

## Good metric sketch

`crm.customer.operations` counter with low-cardinality tags only: `operation=create|get`, `result=success|not_found|validation_error|failure`. Example: `crm.customer.operations{operation="create",result="success"}`. To find *which* customer failed, filter logs by correlationId, not a metric label.

## Debug / design challenge

`customer_create{customerId="CUS-1001"}` → rewrite as `customer_create_total{result="success|failure", reason="validation|conflict"}`. Drop the per-customer label entirely; the customer identity moves to the log line carrying the same correlation ID.

## Predict the output / behavior

`status=ACTIVE|PROSPECT` is usually safe because the status enum has a small, fixed set of values. It can still hurt if the status set grows unbounded over time (many lifecycle states), if it is combined with other labels causing a cardinality explosion through multiplication, or if a free-text/custom status value can ever be injected. Safe only while the value set stays small and closed.

## Scope

Pre-lab only.
