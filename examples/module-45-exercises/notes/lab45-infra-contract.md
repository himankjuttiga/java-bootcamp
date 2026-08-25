# Infra contract

- env / region: dev, single training region (no multi-region sprawl).
- network bounds: app and DB in private subnets; only the load balancer / Ingress is reachable, no direct DB exposure.
- DB: private only, no public IP, reachable in-VPC from the app subnet.
- tags: `application=crm` on every resource (plus env and owner) for cost tracking and cleanup.
- cost ceiling: small dev-sized instances only, a documented monthly cap, destroy when done.
- forbidden: a public database, and any `0.0.0.0/0` ingress on the DB or SSH.
