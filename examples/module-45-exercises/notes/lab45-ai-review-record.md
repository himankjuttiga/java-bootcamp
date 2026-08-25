# AI review outline

| Suggestion | Risk | Human action | Validation |
| --- | --- | --- | --- |
| Public security group (`0.0.0.0/0` on the DB) | exposure, a Lab 40-class finding | rejected, restricted to the app subnet CIDR | terraform validate + plan |
| DB with a public IP | data exposure | rejected, private subnet only | plan review |
| Provider version left floating | drift / supply-chain | hardened, pinned the provider version | validate |

At least one suggestion rejected or hardened: the public SG and the public DB were both rejected.

**Self-mark:** Pass
