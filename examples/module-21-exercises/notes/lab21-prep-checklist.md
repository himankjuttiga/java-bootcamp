# Lab 21 prep checklist

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/lab21-cardinality-antipatterns.md | yes |
| notes/lab21-actuator-allowlist.md | yes |
| notes/lab21-probes.md | yes |
| notes/lab21-metric-sketch-todos.md | yes |
| notes/lab21-alert-runbook.md | yes |

## Scope

Pre-lab only. Prod Actuator exposure? No — health/metrics only in lab; env/beans/loggers stay locked, and production requires auth + network restriction.

## Self mark

Overall prep: Pass
If Fail, revisit: the exercise for whichever note is missing or still has a blank

## Debug / design challenge

If the cardinality notes still allow customerId labels, reopen Exercise 1 (Cardinality Anti-Patterns).

## Predict the output / behavior

`ActuatorIT` — the integration test that proves liveness UP, readiness OUT_OF_SERVICE, and tagged metric increments in the lab.
