# Lab 42 — Deployment runbook

## Prerequisites

- Local k3d cluster `lab42` on k3s `v1.28.15-k3s1`, load balancer `8088:80`.
- Namespace: `crm-training`.
- Lab 41 image `crm-api:lab41` imported into k3d (`k3d image import crm-api:lab41 -c lab42`).
  RepoDigests is empty for a never-pushed local image, so I reference it by tag with
  `imagePullPolicy: IfNotPresent`, not by `@sha256`.
- Postgres reachable from a pod at `host.k3d.internal:5432`, database `crm_lab42`, user `crm`.

## Apply

```bash
# Validate first (client-side dry-run against the cluster schema):
kubectl -n crm-training apply --dry-run=client \
  -f k8s/configmap.yaml -f k8s/deployment.yaml -f k8s/service.yaml -f k8s/ingress.yaml

# Create the Secret out-of-band (never apply secret.example.yaml):
kubectl -n crm-training create secret generic crm-api-secrets \
  --from-literal=CRM_DB_PASSWORD=... \
  --from-literal=CRM_AGENT_A_PASSWORD=... \
  --from-literal=CRM_AGENT_B_PASSWORD=... \
  --dry-run=client -o yaml | kubectl apply -f -

# Apply the listed files (not `-f k8s/`, which would apply secret.example.yaml):
kubectl -n crm-training apply -f k8s/configmap.yaml
kubectl -n crm-training apply -f k8s/deployment.yaml -f k8s/service.yaml -f k8s/ingress.yaml
kubectl -n crm-training rollout status deployment/crm-api --timeout=180s
```

## Smoke

Ingress host is sent as a Host header to the k3d load balancer on `127.0.0.1:8088`
(no hosts-file entry needed):

```bash
curl -fsS -H "Host: crm-api.training.example.test" \
  http://127.0.0.1:8088/actuator/health/readiness
curl -fsS -u agent-a:<password> -H "Host: crm-api.training.example.test" \
  -H "X-Correlation-Id: lab-request-001" \
  "http://127.0.0.1:8088/api/customers?status=ACTIVE"
```

## Rollback rehearsal

```bash
# Bad revision: an image tag that will never pull, so the rollout stalls.
kubectl -n crm-training set image deployment/crm-api crm-api=crm-api:does-not-exist
kubectl -n crm-training rollout status deployment/crm-api --timeout=60s   # will not complete
kubectl -n crm-training rollout history deployment/crm-api

# Undo to the known-good revision:
kubectl -n crm-training rollout undo deployment/crm-api
kubectl -n crm-training rollout status deployment/crm-api --timeout=180s
```

## Residual risk / ownership

- Secret rotation is owned out-of-band; the cluster Secret is the only place the real
  passwords live, never Git.
- `readOnlyRootFilesystem` stays false because Spring Boot writes temp files; revisit with
  an emptyDir at /tmp if that hardening is required.
