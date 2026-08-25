# Terraform checks

1. `terraform fmt` — canonical formatting, no style drift.
2. `terraform init -backend=false` — init providers/modules without touching real state.
3. `terraform validate` (no `-var`) — syntax and internal consistency; validate does not need variable values.
4. `terraform plan -var=environment=dev -var=db_password=unused-local` — read the plan only, do not apply. The password here is a throwaway local value, not a real secret.
5. Never commit: `*.tfstate`, real `*.tfvars`, or cloud keys. Gitignore them.
