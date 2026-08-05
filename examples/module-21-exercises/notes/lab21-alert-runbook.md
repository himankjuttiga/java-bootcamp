# Lab 21 — Alert from create_failure_total

## Signal

`rate(create_failure_total[5m])` exceeds the threshold, sustained FOR 5 minutes. Severity: page. Not a single event — a sustained rate.

## Triage steps

1. Check `/actuator/health` (and `/health/readiness`) — is a dependency like the DB down?
2. Filter structured logs by correlationId (e.g. lab-request-001) to see the failing create requests and their `reason` codes.
3. Confirm scope: one customer segment or all creates; started at a deploy time or gradually.

## CRM check

Reproduce a create for a PROSPECT-shaped payload (CUS-1002) in non-prod. Confirm the failures aren't a bad deploy of validation logic rejecting valid input, versus a genuine downstream/dependency failure.

## Owner

On-call backend / platform (crm-team). Escalate to the service owner if health is UP and the cause isn't found within the runbook.

## Debug / design challenge

If health is UP but failures rise, check next: recent deploys/config changes (a validation regression), the `reason` tag distribution to see *why* creates fail, and the specific failing requests via correlated logs. A healthy process can still reject valid input.

## Predict the output / behavior

Paging on a single failure event is a bad default because one failure is usually noise — a transient blip, a retryable error, or one bad client request. It causes alert fatigue and on-call gets desensitized. Page on a sustained rate instead.

## Scope

Pre-lab only.
