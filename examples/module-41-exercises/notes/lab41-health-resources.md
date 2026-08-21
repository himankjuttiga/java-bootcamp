# Health / resources

- Readiness path: `/actuator/health/readiness` (ready = app started and DB reachable)
- DB down: readiness fails closed, agents don't get a half-ready CRM
- Memory limit note: `docker run -m 512m`; too tight → OOMKill, tune heap
- Non-root UID: 10001
- Graceful stop: SIGTERM drains in-flight `lab-request-001` calls before exit
