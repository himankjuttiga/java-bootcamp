# Lab 39 — Paging and Locking Notes

## Step 1 — Page request

```java
var sort = Sort.by(Sort.Direction.DESC, "createdAt")
               .and(Sort.by(Sort.Direction.DESC, "customerId"));
var pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), sort);
return repository.findByStatus(status, pageable);
```

Two bounds that are not optional:

* **Cap the page size.** `MAX_PAGE_SIZE = 100`. Without it a caller sends `size=1000000` and the
  API cheerfully materialises the table into heap. Spring Boot's
  `spring.data.web.pageable.max-page-size` enforces the same thing at the binding layer.
* **Allow-list the sort fields.** Sort parameters arrive from the client and are turned into
  property names. Accept a fixed set, `createdAt`, `fullName`, `status`, and reject anything else
  with 400. An unbounded sort field is both an information leak and a way to force expensive plans.

Sort properties are **entity field names**, `createdAt`, not column names, `created_at`. Getting
this wrong throws `PropertyReferenceException` at runtime rather than compile time.

## Step 2 — Response

`Page` carries `content`, `totalElements`, `totalPages`, `number` and `size`. Return a DTO, never
the `Page<CustomerEntity>` itself: serialising entities re-exposes every mapping detail, and the
`Page` JSON shape is unstable across Spring Data versions.

`totalElements` costs a second `COUNT(*)` query on every request. On a 50k table that is the
sequential scan Lab 38 measured at 782 buffers. Options, in order of preference for a large list:

| Return type | Cost | Use when |
| --- | --- | --- |
| `Slice` | no count query, only "is there a next page" | infinite scroll, where a total is decoration |
| `Page` | one extra `COUNT(*)` | the UI genuinely renders "page 3 of 47" |
| keyset, from Lab 38 | constant per page, no offset, no count | deep paging over a large list |

## Step 3 — Why the ID tie-breaker

Sorting on `createdAt` alone is not deterministic when two customers share a timestamp, and the
bulk load in Lab 38 produced plenty of ties. Without a tie-breaker the database may order tied rows
differently between two queries, so a row can appear on both page 1 and page 2, or on neither. The
symptom in the UI is a row that vanishes when you page forward, which is nearly impossible to
reproduce on demand.

Adding `customerId` as the final sort key makes the order total, and it also matches the Lab 38
composite index `(status, created_at DESC, customer_id DESC)`, so the page is served without a
separate sort node.

## Step 4 — Optimistic locking

Amina is edited by two support agents at once. Both load `version = 7`.

```text
agent A: UPDATE customer SET status='CLOSED', version=8 WHERE customer_id=1 AND version=7  -> 1 row
agent B: UPDATE customer SET status='ACTIVE', version=8 WHERE customer_id=1 AND version=7  -> 0 rows
         -> ObjectOptimisticLockingFailureException -> HTTP 409
```

Agent B is told to reload and reapply rather than having their edit silently win or silently
vanish. Without `@Version` the second update overwrites the first and nobody ever knows.

Optimistic, not pessimistic, because CRM edits rarely collide. `SELECT ... FOR UPDATE` would hold a
row lock for the whole think-time of a human editing a form, which serialises unrelated work and
invites deadlocks. Pessimistic locking earns its place only where collisions are the norm, such as
decrementing inventory.

Hibernate increments `version` itself. The column exists in the migration with
`version BIGINT NOT NULL DEFAULT 0`, and application code never sets it.

## Step 5 — Correlation

Log the conflict with the correlation id, never as a stack trace to the client:

```java
log.warn("optimistic lock conflict customer={} correlation={}", publicId, correlationId);
```

`lab-request-001` is the same header the SPA has been sending since Lab 35 and the same value the
`customer_status_history` table records in Lab 37. One id ties the browser action, the API log and
the audit row together, which is what makes a support ticket answerable.

## OSIV off: the symptom to recognise

`open-in-view: false` closes the persistence context when the service method returns. Touching a
lazy association after that throws `LazyInitializationException`, which surfaces as a 500 during
JSON serialisation, after the response has begun. The fix is never to switch OSIV back on; it is to
load what the response needs inside the transaction, with a fetch join or a projection, and to map
to a DTO before returning. Leaving OSIV on hides the N+1 queries rather than removing them.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
