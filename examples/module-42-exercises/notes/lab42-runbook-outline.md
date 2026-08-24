# Runbook outline

- Prereqs: namespace + instructor kubeconfig (never commit the kubeconfig or Secret values)
- Apply order: ConfigMap → Secret (created out-of-band) → Deployment → Service → Ingress (`kubectl apply -f k8s/`)
- Verify: `kubectl rollout status deploy/crm-api`; probes Ready
- Smoke: Ingress URL → readiness UP → GET `CUS-1001` (header `lab-request-001`)
- Rollback: `kubectl rollout undo deploy/crm-api`
- Safety: stop before destructive actions; get instructor approval.

Scope: outline only — full apply/smoke is Lab 42.

**Self-mark:** Pass
