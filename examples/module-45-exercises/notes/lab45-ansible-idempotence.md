# Ansible idempotence

| Concern | Approach |
| --- | --- |
| Packages/config | Modules with a desired end state (package/service/template), not raw shell. |
| Second run | A re-run reports `changed=0` when nothing needs changing, proving idempotence. |
| Inventory | Commit an example inventory only, no real hosts or secrets. |

`changed=0` on the second run is the evidence the play is safe to re-apply.
