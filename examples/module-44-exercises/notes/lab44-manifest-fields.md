# Artifact manifest fields

- `version`: semver of the release, e.g. `0.0.1` behind tag `v0.0.1`.
- `gitCommit`: the `commit=<GITHUB_SHA>` line from the Lab 43 `SHA256SUMS`.
- `jarSha256`: required, copied from the Lab 43 `SHA256SUMS` (the `crm-jar` artifact). Never rebuilt on the deploy host.
- `imageDigest`: optional, null unless the image was pushed to a registry. The local k3d image has no RepoDigest.
- `knownGoodPrevious.jarSha256`: the previous release's jarSha256, recorded before promote so rollback has a target.
- No secrets in this file.
