# Lab 41 — Container runbook

## Build

```bash
cd ~/java-bootcamp/examples/lab41-crm
docker build --pull -t crm-api:lab41 .
docker image inspect crm-api:lab41 --format 'id={{.Id}} size={{.Size}} user={{json .Config.User}}'
```

Record the image id, size, and `User` (expect `10001`). Capture the digest after a push:
`docker image inspect crm-api:lab41 --format '{{index .RepoDigests 0}}'`.

## Run

The DB (`crm-postgres`) is a sibling container, so join its network and use its service name as
the host — `localhost` inside the container is the container itself.

```bash
docker run --rm --name crm-lab41 \
  --network lab37-crm_default \
  --memory=512m --env-file .env -p 8080:8080 \
  crm-api:lab41
```

`.env` (gitignored) holds the real values; `.env.example` carries placeholders only.

## Verify

```bash
# Load agent credentials from .env (never hardcode them here):
set -a; source .env; set +a

# Readiness (no auth):
curl -fsS http://127.0.0.1:8080/actuator/health/readiness            # expect {"status":"UP"}

# CRM smoke — create then get CUS-1001 as agent-a, with the correlation header:
curl -fsS -u "agent-a:$CRM_AGENT_A_PASSWORD" -H "X-Correlation-Id: lab-request-001" \
  -H "Content-Type: application/json" -X POST http://127.0.0.1:8080/api/customers \
  -d '{"publicId":"CUS-1001","fullName":"Amina Khan","email":"amina.khan@example.test","status":"ACTIVE"}'
curl -fsS -u "agent-a:$CRM_AGENT_A_PASSWORD" http://127.0.0.1:8080/api/customers/CUS-1001

# User inside the container (expect uid=10001):
docker exec crm-lab41 id
```

## Stop / graceful shutdown

```bash
docker stop --time 20 crm-lab41
docker logs crm-lab41 --tail 20   # orderly Spring shutdown within the timeout
```

## Failure case

Run with a wrong `CRM_DB_HOST` (or bad port): readiness returns DOWN and the container fails
closed instead of serving a half-ready CRM. Remove the failed container after capturing logs.

## Registry (notes only — no credentials)

- Tag by version + git SHA, not only `:latest`:
  `GIT_SHA=$(git rev-parse --short HEAD); docker tag crm-api:lab41 crm-api:1.0.0-$GIT_SHA`
- `docker login` happens outside source control; credentials never in Git.
- Lab 42 promotes by **digest** (`sha256:...`), not by the mutable `:latest` tag.
- Cleanup: remove stale local tags with `docker image rm` once the digest is recorded.
