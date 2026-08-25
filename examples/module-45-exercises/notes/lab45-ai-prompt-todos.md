# AI prompt constraints

- Stay inside the infra contract (private DB, tagged, cost ceiling); reject anything outside it.
- Pin provider versions, no floating `latest`.
- No public database, no `0.0.0.0/0` ingress.
- No secrets in HCL; variables and secret values stay out of the committed files.
- Every AI output is reviewed by a human before plan/apply, never applied blind.
