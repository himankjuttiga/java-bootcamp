# Lab 25 readiness checklist

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/layers.md | yes |
| notes/package-tree.md | yes |
| notes/lab25-service-todo-skeleton.md | yes |
| notes/ai-review-policy.md | yes |
| notes/service-test-plan.md | yes |

## Scope

Pre-lab only. Controller may import repository? No — the controller depends on the service only; the service owns the repository.

## Self mark

Overall prep: Pass
If Fail, revisit: the exercise for whichever note is missing or still has a blank

## Debug / design challenge

If `layers.md` still allows controller → repository, reopen Exercise 1 (Layer Boundary Quiz).

## Predict the output / behavior

No — Lab 27 `@Transactional` is not required for a Lab 25 timed-path Pass. Lab 25 is about layer separation with an in-memory store; transactions come later.
