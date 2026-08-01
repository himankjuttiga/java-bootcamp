# Lab 18 — Stub vs Verify

## Step 1 — Stub
`when(repo.findById("CUS-1002")).thenReturn(Optional.of(raviProspect))` — arrange. This feeds the service a known input (Ravi CUS-1002 as PROSPECT) so the activate logic has something deterministic to act on.

## Step 2 — Verify
`verify(repo).save(argThat(c -> c.getStatus() == ACTIVE))` — assert the collaboration happened. It proves changeStatus actually persisted the activated customer, not just returned a value.

## Step 3 — Both
Stubs feed inputs into the unit under test; verifies prove the expected side-effect calls were made on collaborators.

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.