# Lab 39 — Fill JPA TODOs

## Step 1 — Paste

```java
@Entity
@Table(name = "customer")
class CustomerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "customer_id")
  private Long customerId;                 // surrogate, database-assigned

  @Column(name = "public_id", nullable = false, unique = true)
  private String publicId;                 // CUS-1001, the API identifier

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String status;

  @Version
  private Long version;                    // optimistic lock

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();
}

interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
  Optional<CustomerEntity> findByPublicId(String publicId);
  Page<CustomerEntity> findByStatus(String status, Pageable pageable);
}
```

```yaml
spring:
  datasource:
    url:      ${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/crm}
    username: ${SPRING_DATASOURCE_USERNAME:crm_app}
    password: ${SPRING_DATASOURCE_PASSWORD}      # no default, no committed value
  jpa:
    hibernate:
      ddl-auto: validate
    open-in-view: false
    properties:
      hibernate.jdbc.time_zone: UTC
  flyway:
    enabled: true
```

## Step 2 — Fill

| Blank | Value | Why |
| --- | --- | --- |
| `@Id` field | `Long customerId` | matches `BIGSERIAL`. The template's `String customerId` contradicts the Lab 37 DDL and the starter |
| name field | `fullName` mapped to `full_name` | explicit `@Column` survives a naming-strategy change |
| `@Version` field | `Long version` | the migration adds a `version` column for it |
| repository page arg | `Pageable` | returns `Page`, which carries `totalElements` |
| datasource url | `jdbc:postgresql://localhost:5432/crm` | the Lab 37 container, database `crm` |
| `ddl-auto` | `validate` | Flyway owns the schema; Hibernate only checks agreement |
| `flyway.enabled` | `true` | migrations are the source of truth |

## Step 3 — Usage TODO

```java
// TODO: service.load("CUS-1001") -> Optional<CustomerEntity> for Amina
Optional<CustomerEntity> amina = repository.findByPublicId("CUS-1001");
// 404 when empty; never .get() without checking
```

## Step 4 — Locking note

Two writers load Ravi at `version = 3`. The first save succeeds and the row becomes `version = 4`.
The second save issues `UPDATE ... WHERE customer_id = ? AND version = 3`, matches zero rows, and
Hibernate raises `ObjectOptimisticLockingFailureException`. Nobody's edit is silently overwritten,
which is exactly what lost-update looks like without `@Version`.

The API turns that into **409 Conflict**, and the SPA from Lab 36 already distinguishes 409 from
401 and 403, so it can tell the user to reload and reapply their change rather than logging them
out.

## Where conflicts become HTTP 409

Two different exceptions, one status code, handled in `ApiExceptionHandler`:

| Cause | Exception | Status |
| --- | --- | --- |
| Duplicate email or public_id, SQLSTATE 23505 | `DataIntegrityViolationException` | 409 |
| Stale `@Version` on update | `ObjectOptimisticLockingFailureException` | 409 |

The handler must not echo the exception message. Hibernate's text carries the SQL statement, the
constraint name and sometimes the parameter values, which is precisely the server internals Lab 36
established should never reach a browser. Map to a safe body naming the field, and log the detail
with the correlation id `lab-request-001`.

## Secrets

`spring.datasource.password` has no default in the YAML and no value committed. It comes from the
environment, `SPRING_DATASOURCE_PASSWORD`, sourced from a gitignored `.env`. A password committed
in `application.yml` is in the repository history permanently, so the fix is not to delete the line
in a later commit but to rotate the credential and move it to the environment.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
