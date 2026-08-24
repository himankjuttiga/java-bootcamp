# Manifest map

| Kind | Purpose |
| --- | --- |
| Namespace | per-student boundary on the shared k3s cluster |
| Deployment | crm-api pod template + probes, digest-pinned image |
| Service | ClusterIP, selects `app=crm-api` pods on port 8080 |
| ConfigMap | non-secret config (DB host/port/name, profile, log level) |
| Secret (ref) | DB + agent passwords, created out-of-band, never in Git |
| Ingress | Traefik host/path + TLS to the Service |

Labels: `app=crm-api`, `lab=42`. Image is digest-pinned from Lab 41 (`crm-api@sha256:...`), not `:latest`.
