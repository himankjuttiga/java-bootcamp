# Lab 42 — Deployment runbook

## Prerequisites

- kubeconfig for instructor k3s (not in Git)
- Namespace: `<your-namespace>` (assigned per student)
- Image digest from Lab 41: `crm-api@sha256:707a818adce2900b1aa0f0158cd1dd7fa1c1a961914fefe0476672ff069abe21`

## Apply

```bash
# Validate first (client-side, no cluster needed):
kubectl apply --dry-run=client --validate=false -f k8s/

# Create the Secret out-of-band (never apply secret.example.yaml with real values):
kubectl create secret generic crm-api-secrets -n <ns> \
  --from-literal=CRM_DB_PASSWORD=... \
  --from-literal=CRM_AGENT_A_PASSWORD=... \
  --from-literal=CRM_AGENT_B_PASSWORD=...

kubectl apply -f k8s/configmap.yaml -n <ns>
kubectl apply -f k8s/deployment.yaml -n <ns>
kubectl apply -f k8s/service.yaml -n <ns>
kubectl apply -f k8s/ingress.yaml -n <ns>
kubectl rollout status deployment/crm-api -n <ns>
```

## Smoke

- Health via Ingress: `curl -fsS https://crm-api.lab42.example.test/actuator/health/readiness` → `UP`
- Customer `CUS-1001` with header `X-Correlation-Id: lab-request-001`

## Rollback rehearsal

```bash
# Introduce a bad revision (non-existent digest), watch it fail to become Ready:
kubectl set image deployment/crm-api crm-api=crm-api@sha256:0000000000000000000000000000000000000000000000000000000000000000 -n <ns>
kubectl rollout status deployment/crm-api -n <ns>   # will not complete

# Undo to the known-good revision:
kubectl rollout undo deployment/crm-api -n <ns>
kubectl rollout status deployment/crm-api -n <ns>
```
