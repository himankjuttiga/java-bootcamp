# Lab 17 — Meaningful Asserts

## Step 1 — Weak
`assertNotNull(result)` after activate — weak. It passes as long as any object is returned, even one with the wrong id or status, so it proves almost nothing about the behavior.

## Step 2 — Strong
After activating Ravi (CUS-1002 PROSPECT -> ACTIVE):

    assertEquals("CUS-1002", result.getCustomerId());
    assertEquals(CustomerStatus.ACTIVE, result.getStatus());

These pin both identity and the state transition, so a wrong id or an unchanged status fails the test immediately.

## Step 3 — Exception assert
Activating Amina under the illegal policy (CUS-1001 ACTIVE -> PROSPECT):

    BusinessException ex = assertThrows(BusinessException.class,
        () -> service.changeStatus("CUS-1001", CustomerStatus.PROSPECT, "lab-request-001"));
    assertEquals(409, ex.getStatusHint());
    assertEquals(CustomerStatus.ACTIVE,
        service.findById("CUS-1001").orElseThrow().getStatus()); // unchanged

## Step 4 — Prep only
*Prepare for Lab 17; do not complete full suite now.*

## Scope
Pre-lab only — do not finish the full graded lab in this exercise.