# Lab 21 — Fill Metric Sketch TODOs

Success counter: `crm.customer.operations{operation="create",result="success"}` (create_success_total)
Failure counter: `crm.customer.operations{operation="create",result="failure"}` (create_failure_total)
Forbidden label: customerId (also correlationId, traceId, raw error text)
Alert name: CrmCreateFailuresHigh
Alert threshold idea: `rate(create_failure_total[5m])` above threshold, sustained FOR 5m
First responder action: check /actuator/health, then filter logs by correlationId (e.g. lab-request-001) to find the failing requests

## Debug / design challenge

The alert must use a **rate over N minutes**, not a raw forever-total. A counter only increases and resets on restart, so a raw total says nothing about *current* health — a rate like `rate(create_failure_total[5m])` measures how fast failures are happening right now, which is what you page on.

## Predict the output / behavior

The first responder looks at logs, not metric labels, because customerId is deliberately kept *out* of metrics to avoid high cardinality. The metric only tells you failures are rising; the specific customer (CUS-1001) lives in the correlated log line, reached via the correlation ID.

## Scope

Pre-lab only.
