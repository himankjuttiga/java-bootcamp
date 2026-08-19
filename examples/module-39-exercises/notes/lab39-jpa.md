# Lab 39 — Entity Mapping

## Reference: CUSTOMER

| Column (Lab 37/38 DDL) | Java field and annotation |
| --- | --- |
| `customer_id BIGSERIAL` | `@Id @GeneratedValue(strategy = IDENTITY) @Column(name = "customer_id") Long customerId` |
| `public_id VARCHAR(32) UNIQUE` | `@Column(name = "public_id", nullable = false, unique = true) String publicId` |
| `full_name VARCHAR(200)` | `@Column(name = "full_name", nullable = false) String fullName` |
| `email VARCHAR(320) UNIQUE` | `@Column(nullable = false, unique = true) String email` |
| `status VARCHAR(32)` | `@Column(nullable = false) String status` |
| `version BIGINT` | `@Version Long version` |
| `created_at TIMESTAMPTZ` | `@Column(name = "created_at", nullable = false) Instant createdAt` |
| `updated_at TIMESTAMPTZ` | `@Column(name = "updated_at", nullable = false) Instant updatedAt` |

`GenerationType.IDENTITY` matches `BIGSERIAL`: the database assigns the id. It does cost one thing
worth knowing, that Hibernate cannot batch inserts for IDENTITY ids, because it must round-trip to
learn each generated key. `SEQUENCE` with an allocation size batches better; `BIGSERIAL` is what
Lab 37 built, so `IDENTITY` is the honest mapping.

`TIMESTAMPTZ` maps to `Instant`, never `LocalDateTime`. `LocalDateTime` has no offset, so the
moment a second machine in another zone writes a row the ordering becomes fiction. Pair it with
`hibernate.jdbc.time_zone: UTC` so the driver stops applying the JVM default.

## Step 2 — Account

| Column | Java field |
| --- | --- |
| `account_id BIGSERIAL` | `@Id @GeneratedValue(IDENTITY) Long accountId` |
| `customer_id BIGINT NOT NULL` | `@Column(name = "customer_id", nullable = false) Long customerId` |
| `account_number VARCHAR(32) UNIQUE` | `@Column(name = "account_number", nullable = false, unique = true) String accountNumber` |
| `balance_cents BIGINT` | `@Column(name = "balance_cents", nullable = false) long balanceCents` |
| `version BIGINT` | `@Version Long version` |
| `opened_at TIMESTAMPTZ` | `@Column(name = "opened_at", nullable = false) Instant openedAt` |

The starter maps the foreign key as a plain `Long customerId`, not as `@ManyToOne CustomerEntity`.
That is a deliberate simplification and it is worth being able to defend:

| Approach | Gains | Costs |
| --- | --- | --- |
| Plain `Long customerId` | no lazy loading, no proxy surprises, no accidental N+1, trivially serialisable | you must fetch the customer yourself when you need it |
| `@ManyToOne(fetch = LAZY)` | object graph navigation, cascade options | lazy proxies explode outside the transaction when OSIV is off, and careless list rendering becomes N+1 |

With `open-in-view: false`, which this lab sets, a lazy `@ManyToOne` touched in a controller throws
`LazyInitializationException`. The `Long` mapping sidesteps that entirely.

Money stays `long balanceCents`, exact integer minor units. Answering the predict question
directly: never `double`. Binary floating point cannot represent 0.10 exactly, so balances drift
once you add them. If the schema had used `NUMERIC(19,2)`, the field would be `BigDecimal`, never
`double`.

## Step 3 — Naming

Columns are `snake_case`, fields are `camelCase`, and the bridge is explicit `@Column(name = ...)`
on every field where the two differ. Spring Boot's default `CamelCaseToUnderscoresNamingStrategy`
would derive most of them correctly, but explicit names survive a strategy change and make the
mapping greppable against the Flyway migration.

`ddl-auto: validate`, never `create`, `create-drop` or `update`. Flyway owns the schema; Hibernate
only checks that the classes agree with it and refuses to start when they do not. That failure at
boot is the feature: a mapping drift is caught before the first request rather than at 3am.

## Step 4 — Fixture

```text
CustomerEntity{ customerId=1, publicId="CUS-1001", fullName="Amina Khan",
                email="amina@example.com", status="ACTIVE", version=0 }
CustomerEntity{ customerId=2, publicId="CUS-1002", fullName="Ravi Singh",
                email="ravi@example.com",  status="PROSPECT", version=0 }
```

`customerId` is assigned by the database and is internal. `publicId` is what the API and the SPA
from Labs 33 to 36 use, which is why `findByPublicId` exists on the repository.

## Entities are not API responses

The debug question asks about exposing entities directly. Four concrete risks:

1. **Leakage.** Every field ships, including `version`, internal ids, and any column added later.
2. **Coupling.** A column rename becomes a breaking API change, so the schema can no longer evolve
   independently of clients.
3. **Lazy blowups.** Jackson serialising a detached entity triggers lazy loads outside the
   transaction, which with OSIV off is an exception mid-response, after the status line was sent.
4. **Unbounded writes.** Binding request JSON straight onto an entity lets a caller set fields the
   API never meant to expose, including `status` or `version`.

The fix is a DTO per direction: a request record for input, a response record for output, mapped in
the service layer.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
