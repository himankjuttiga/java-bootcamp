# Staging smoke

- Health / readiness: GET /actuator/health/readiness returns UP (permitAll, no creds).
- List API: GET /api/customers?status=ACTIVE as agent-a returns 200 (the list, not /{id}).
- Correlation: send `X-Correlation-Id: lab-request-001` so the run is traceable in logs.
- If live on k3d: Host header `crm-api.training.example.test` to `http://127.0.0.1:8088`.
- Evidence: redacted screenshot under `notes/screenshots/lab-44/`, no tokens or kubeconfig.

**Self-mark:** Pass
