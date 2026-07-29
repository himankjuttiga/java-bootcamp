# Lab 11 pre-lab -- trivial vs real asserts

## Trivial (reject these -- false confidence)
1. assertNotNull(customer);
2. assertTrue(true);
Why weak: they pass even if status, id, or business rules are wrong.

## Meaningful (prefer these)
1. assertEquals(CustomerStatus.ACTIVE, amina.getStatus());  // CUS-1001 Amina
2. assertEquals(CustomerStatus.PROSPECT, ravi.getStatus()); // CUS-1002 Ravi

## Review rule (one sentence)
Reject AI tests that never mention domain values (CUS-1001/CUS-1002, ACTIVE/PROSPECT) or outcomes.

## Scope
Pre-lab only -- do not finish Lab 11.