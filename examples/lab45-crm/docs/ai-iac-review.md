# Lab 45 — AI IaC review record

## Contract (fill first)

- Allowed environments: `dev` | `test` | `staging` (no prod apply)
- Forbidden: public DB, `0.0.0.0/0` on DB/SSH, secrets in Git, unpinned providers
- Laptop path: `null_resource` only — do not apply to Lab 42 k3d

## Prompts used (summarized) — entry `lab45-001`

Asked an AI to draft Terraform for a CRM stack (VPC, private DB, app runtime), constrained to:
pinned providers, `dev`/`test`/`staging` only, private DB with no public IP, no `0.0.0.0/0`, no
secrets in HCL, and outputs limited to non-secret values. No real credentials were put in the prompt.

## AI suggestions accepted

| Item | Why accepted |
| ---- | ------------ |
| Pin `hashicorp/null` to `~> 3.2` | Avoids silent provider drift / supply-chain surprise |
| `application=crm` + `cost_center` tags | Cost tracking and cleanup on every resource |
| `environment` validation (dev/test/staging) | Enforces the contract, blocks a prod apply at plan time |
| Outputs limited to `environment` / `region` | No secret or endpoint credential leaves the module |

## AI suggestions rejected or hardened

| Item | Risk | Human change |
| ---- | ---- | ------------ |
| Public DB (public IP) | Data exposure, Lab 40-class finding | Rejected — private subnet only, no public IP |
| `0.0.0.0/0` on DB/SSH ingress | Open attack surface | Rejected — restrict to the app subnet CIDR |
| Floating/latest provider version | Drift / supply-chain | Hardened — pinned `~> 3.2` |

At least one rejection: the public DB and the `0.0.0.0/0` ingress were both rejected.

## Validation evidence

- `terraform fmt -recursive`: no changes needed (exit 0).
- `terraform init -backend=false`: success, `hashicorp/null` 3.2.x installed.
- `terraform validate` (**no** `-var`): `Success! The configuration is valid.`
- `terraform plan -var='environment=dev' -var='db_password=unused-local'`: `Plan: 1 to add, 0 to change, 0 to destroy` (null_resource sketch, no cloud, no apply).
- Ansible `--syntax-check`: not run — `ansible-playbook` is not installed on this Windows PATH. Residual risk recorded below. `site.yml` is written idempotent (group/user/package all `state: present`).

## Residual risks

- Ansible play not syntax-checked on Windows (owner: me; expiry: run `--syntax-check` from the lab root when Ansible is available, or in CI). Low risk, tasks are declarative desired-state.
- `null_resource` is a sketch, not real infrastructure; real modules require a human threat review before any apply, and no prod apply.
- No customer PII in IaC. `db_password` stays in local tfvars / a secret store, never committed.
