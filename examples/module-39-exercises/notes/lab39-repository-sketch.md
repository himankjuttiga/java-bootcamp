# Lab 39 — Repository Sketch

## Step 1 — CustomerRepository

```java
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

  Optional<CustomerEntity> findByPublicId(String publicId);   // GET /api/customers/CUS-1001

  Optional<CustomerEntity> findByEmail(String email);         // login and duplicate check

  Page<CustomerEntity> findByStatus(String status, Pageable pageable);  // the SPA list view

  boolean existsByEmail(String email);                        // cheap pre-check, not the guarantee
}
```

The type parameter is `Long`, not `String`: the id is the surrogate `customer_id`, matching the
Lab 37 schema. The exercise template suggests `JpaRepository<CustomerEntity, String>` with
`@Id String customerId`, which does not match the starter or the DDL. Business lookups go through
`findByPublicId`, which is what keeps `CUS-1001` immutable and public while the surrogate stays
internal.

`existsByEmail` is a convenience, not a control. Between the check and the insert another request
can win, so the unique constraint remains the guarantee and the 409 path still has to exist.

## Step 2 — AccountRepository

```java
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

  List<AccountEntity> findByCustomerId(Long customerId);      // Amina's accounts

  Optional<AccountEntity> findByAccountNumber(String accountNumber);
}
```

`findByCustomerId(Long)` takes the surrogate, because that is what `account.customer_id` holds. To
serve "accounts for CUS-1001" the service resolves the public id first, or the repository declares
a join explicitly:

```java
@Query("select a from AccountEntity a join CustomerEntity c on c.customerId = a.customerId "
     + "where c.publicId = :publicId")
List<AccountEntity> findByCustomerPublicId(@Param("publicId") String publicId);
```

This is the query Lab 38's `ix_account_customer` exists to serve. Ravi returns an empty list, which
is correct rather than an error: `1 : 0..N` all the way from the ER sketch to the repository.

## Step 3 — Derived versus @Query

| Use a derived name | Use `@Query` |
| --- | --- |
| One or two predicates: `findByStatus`, `findByEmail` | three or more conditions, where the method name stops being readable |
| The mapping is obvious from the name | joins across entities, or projections onto a DTO |
| No SQL knowledge needed to review it | you need a specific plan shape, an aggregate, or a `LIMIT` the derivation cannot express |

The tipping point is legibility. `findByStatusAndCreatedAtAfterAndEmailContainingIgnoreCase` is a
name nobody reads twice; the same thing as a `@Query` with JPQL, or a Specification for genuinely
dynamic filters, is easier to review. Neither is ever string concatenation: both bind parameters,
so injection is not a live risk in either form. That is exactly why repositories exist here rather
than hand-built SQL.

## Step 4 — Service boundary

```text
Controller  -> DTOs in and out, HTTP status, validation annotations
Service     -> @Transactional, business rules, entity to DTO mapping
Repository  -> data access only, no rules
Entity      -> mapping only, never returned from a controller
```

Answering the debug question: business logic does not belong in the repository interface. A
repository method is a query, and a query with a rule baked into it cannot be reused by any caller
that needs the rule to differ. Rules such as "a CLOSED customer cannot open an account" or "reject
a status transition that skips ACTIVE" live in the service, inside the transaction, where they can
read what they need and fail atomically.

Transaction boundaries sit on the service too, never on the controller and never on the repository:

* `@Transactional` on writes, so a multi-step change commits or rolls back as one unit.
* `@Transactional(readOnly = true)` on reads, which lets Hibernate skip dirty checking.
* With `open-in-view: false`, the transaction ends when the service method returns, so anything the
  response needs must be loaded before then. That is the constraint that makes DTO mapping in the
  service a requirement rather than a style preference.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
