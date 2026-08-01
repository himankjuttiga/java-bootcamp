# AI / Refactor Review Notes — Lab 12

Copilot was not used for this refactor pass. Per the lab, this is the manual-review
substitute: the key refactor decisions I made by hand and why, with the risks I rejected.

## lab12-001 — Storage: Map keyed by ID vs List scan
- Decision: replaced `List data` + index loop with `Map<String, Customer>` keyed by customerId.
- Reason: the old `get` used `==` on Strings, so a lookup by a value-equal but non-identical
  id (e.g. `new String("CUS-1001")`) returned null and support could not find Amina.
  A Map keyed by id fixes both the lookup and duplicate detection (`containsKey`) at once.
- Verdict: accepted. Verified by the `getByValueEqualIdWorks` and `duplicateIdRejected` tests.

## lab12-002 — Rejected: silent upsert on duplicate
- Considered: make `createCustomer` overwrite an existing id instead of failing.
- Verdict: rejected. That hides data loss and changes the documented contract. The lab
  requires a clear `IllegalStateException` on duplicate, so create stays strict and status
  changes go only through `updateStatus`.

## lab12-003 — Errors: exceptions with correlation vs null returns
- Decision: throw `IllegalArgumentException` (blank / unknown) and `IllegalStateException`
  (duplicate) with `correlationId=lab-request-001` in the message, instead of returning null.
- Reason: null returns pushed NullPointerExceptions onto callers and gave support no trace id.
  Exceptions fail loudly at the boundary and carry the correlation id for diagnosis.
- Verdict: accepted.

## Risk I watched for
- Any suggestion to add `@Service`, `@Entity`, or JPA imports would be rejected — this project
  has no Spring or JPA dependency and must compile as plain Java 21. None were added.