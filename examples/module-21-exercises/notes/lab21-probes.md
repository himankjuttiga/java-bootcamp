# Lab 21 — Liveness vs Readiness

## Liveness

Answers "is the process stuck or irrecoverable?" A failed liveness probe tells the orchestrator to **restart** the pod. Use it for unrecoverable states like deadlocked request threads or a wedged JVM. Do not check external dependencies that may recover on their own.

## Readiness

Answers "can this instance serve traffic right now?" A failed readiness probe tells the load balancer to **stop routing** to this pod while keeping the process alive. Use it when a dependency like the database is down.

## Wrong mix

Wiring a transient DB outage into liveness restarts every pod on a database blip — removing capacity exactly when the system is already stressed, and risking a restart death spiral. A DB outage should fail readiness, not liveness.

## Lab expectation

Toggle the `CrmReadinessIndicator` to `OUT_OF_SERVICE`; `/actuator/health/readiness` reports `OUT_OF_SERVICE` while `/actuator/health/liveness` stays `UP` — traffic is shed, the process is not killed.

## Debug / design challenge

If readiness is DOWN and liveness UP, Kubernetes should **not** kill the pod. It stops routing traffic and waits for readiness to recover; the process keeps running so it can rejoin the load balancer once the dependency is back.

## Predict the output / behavior

`CrmReadinessIndicator` `OUT_OF_SERVICE` maps to the **readiness** probe — it gates traffic, not the process lifecycle.

## Scope

Pre-lab only.
