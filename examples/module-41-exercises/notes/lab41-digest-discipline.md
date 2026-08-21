# Digest discipline

- Tag: `crm-api:lab41`
- Why not :latest-only: `:latest` is mutable — two pulls a week apart can be different bits; digests are immutable so Lab 42/44 promote by digest
- Record digest command: `docker image inspect crm-api:lab41 --format '{{index .RepoDigests 0}}'`
- Runbook (`docs/container-runbook.md`) headings: build, inspect user, run, stop, digest capture
