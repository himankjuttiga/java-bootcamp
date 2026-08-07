# Lab 26 readiness checklist

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/profiles.md | yes |
| notes/lab26-profile-yaml-todos.md | yes |
| notes/northstar-props.md | yes |
| notes/override-order.md | yes |
| notes/activation-commands.md | yes |

## Scope

Pre-lab only. Real secrets in Git? no — all secrets are `${ENV}` references and `.env` is gitignored (only `.env.example` placeholders committed). Prod fail-fast understood: missing required env vars must abort startup, never connect with a blank password.

## Self mark

Overall prep: Pass

If Fail, revisit: whichever exercise the missing/blank artifact belongs to.

## Answers to the prompts

- **Literal password still in YAML TODOs:** reopen Exercise 2 (Profile YAML TODOs) and replace it with a `${ENV}` reference.
- **Is HashiCorp Vault required for the timed-path Pass?** No. Environment-variable secrets are sufficient for Lab 26; Vault / cloud secret managers are optional enterprise extensions.
