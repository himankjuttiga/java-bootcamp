# Lab 18 — Mockito Anti-Patterns

## Reference
| Anti-pattern | Better |
| --- | --- |
| Mock the SUT | Mock collaborators only |
| Unnecessary stubbing | Stub what is used |
| verifyNoMoreInteractions always | Use when interaction surface is critical |
| Mock a value/data object (Customer) | Use real state objects for Amina/Ravi |

## Step 2 — AI reject rule
Reject suggestions that mock CustomerService while testing CustomerService; you only mock its collaborators (repository, notifier).

## Step 3 — Fixture
Prefer real Customer state objects for Amina (CUS-1001) and Ravi (CUS-1002) over mocking getters needlessly.

## Step 4 — Boundary
ArgumentCaptor deep practice continues in the timed lab; this is the preview.

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.