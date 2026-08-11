# Lab 28 — Production IdP Checklist

## IdP note

Prefer an enterprise Identity Provider / OAuth2 (Keycloak, Okta, Entra ID, Cognito) in production. The lab's hand-rolled JWT is teaching mode only, not a production auth server.

## Key rotation

Store signing keys in a secret manager (Vault, AWS Secrets Manager). Rotate on a schedule and immediately on any suspected compromise. Support overlapping keys (key id / `kid`) so in-flight tokens validate during rotation.

## Transport / TTL

HTTPS only — never send Bearer tokens over plain HTTP. Keep access-token TTL short (minutes) and use longer-lived, revocable refresh tokens for continuity.

## Logging hygiene

Audit failed logins and authorization denials. Never log raw Bearer tokens, JWT secrets, or passwords. Apply least privilege and periodically review ADMIN grants.

## Answers to the prompts

- **Does Lab 28 require standing up Keycloak?** No — the timed-path Pass uses the lab's own JWT flow; an external IdP is awareness/production guidance, not a lab requirement.
- **If a JWT signing secret was committed:** treat it as compromised — rotate the secret immediately, invalidate tokens signed with the old key, purge it from history, and move it to a secret manager / env var.

## Scope

Pre-lab only. No real secrets.
