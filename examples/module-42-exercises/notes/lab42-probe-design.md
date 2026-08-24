# Probes

| Probe | Path (port 8080) | Notes |
| --- | --- | --- |
| startup | /actuator/health/liveness | allows slow boot before readiness/liveness start |
| readiness | /actuator/health/readiness | includes the DB check; gates traffic |
| liveness | /actuator/health/liveness | restarts the pod only if the process is wedged |

If readiness fails while liveness stays up: the pod is not restarted, it is removed from the Service
endpoints, so agents get no traffic (503 at the Ingress) until the DB dependency recovers.
