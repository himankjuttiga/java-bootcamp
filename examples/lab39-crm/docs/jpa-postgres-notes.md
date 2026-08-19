# Lab 39 — JPA / PostgreSQL notes

## Flyway vs ddl-auto

Flyway owns the schema; Hibernate is set to `validate` and owns nothing.

`validate` compares every mapping against the live schema at startup and refuses to boot on a
mismatch. That failure is the feature: a renamed column or a forgotten migration is caught before
the first request rather than at 3am on the one code path that touches it.

| Setting | What it does | Verdict |
| --- | --- | --- |
| `validate` | checks mappings, changes nothing | **used here** |
| `none` | no check at all | acceptable, but gives up a free safety net |
| `update` | alters tables to fit the entities | never: it cannot drop or rename, so it accumulates orphan columns silently and leaves no record of what changed |
| `create` / `create-drop` | rebuilds the schema, dropping data | never outside a throwaway test database |

Mixing Flyway with `ddl-auto: update` is worse than either alone: two systems believe they own the
schema, Hibernate alters a table Flyway does not know about, and the next migration collides with
an object it did not create.

**Checksum mismatch.** Flyway records a checksum for each applied file. Editing `V1` after it has
run anywhere makes startup fail, correctly: the file already ran elsewhere, so two environments
would share a version number and differ in schema. The fix is a new `V2__*.sql` that changes things
forward. `flyway repair` only rewrites the recorded checksum, which is right after a
whitespace-only reformat and wrong as a way to smuggle in an edit. Never edit
`flyway_schema_history` by hand.

The Lab 38 indexes live in `V1` here because this project's `V1` had never been applied anywhere
when they were added. Once it has run, the same indexes would have to arrive as `V2`.

## Optimistic locking

`@Version Long version` on both entities, backed by `version BIGINT NOT NULL DEFAULT 0`.

Two agents load Amina at version 7. The first save issues
`UPDATE customer SET ..., version = 8 WHERE customer_id = ? AND version = 7` and affects one row.
The second issues the same statement, affects zero rows, and Hibernate raises
`ObjectOptimisticLockingFailureException`. Without `@Version` the second write would overwrite the
first and nobody would ever know: that is the lost-update problem.

Optimistic rather than pessimistic because CRM edits rarely collide. `SELECT ... FOR UPDATE` would
hold a row lock for the whole time a human stares at a form, serialising unrelated work and
inviting deadlocks. Pessimistic locking earns its place where collisions are the norm, such as
decrementing inventory.

## Two causes, one status code

| Cause | Exception | HTTP |
| --- | --- | --- |
| duplicate email or public_id, SQLSTATE 23505 | `DataIntegrityViolationException` | 409 |
| stale `@Version` | `OptimisticLockingFailureException` | 409 |

Both answer 409 with a `ProblemDetail` body carrying a `correlationId` and no server internals.
Hibernate's message contains the SQL statement, the constraint name and sometimes parameter values,
so it is logged and never returned, which is the same rule Lab 36 established for the browser.

The SPA from Lab 36 already distinguishes 409 from 401 and 403, so a conflict tells the user to
reload and reapply rather than logging them out.

## open-in-view: false

The persistence context closes when the service method returns. Touching a lazy association after
that throws `LazyInitializationException` during JSON serialisation, after the response has already
begun. The fix is never to switch OSIV back on, which only hides N+1 queries in the view layer; it
is to load what the response needs inside the transaction and map to a DTO there.

That is also why `AccountEntity` holds a plain `Long customerId` rather than
`@ManyToOne CustomerEntity`: there is no lazy proxy to explode and no association for a list render
to walk one row at a time.

## Types that matter

| Column | Java | Why not the obvious alternative |
| --- | --- | --- |
| `balance_cents BIGINT` | `long` | never `double`: binary floating point cannot represent 0.10, so balances drift once summed. A `NUMERIC(19,2)` column would map to `BigDecimal` |
| `created_at TIMESTAMPTZ` | `Instant` | `LocalDateTime` has no offset, so two machines in different zones produce indistinguishable rows |
| `customer_id BIGSERIAL` | `Long` with `IDENTITY` | matches the database-assigned key. IDENTITY blocks Hibernate's insert batching, which `SEQUENCE` would allow; `BIGSERIAL` is what Labs 37 and 38 built |

`hibernate.jdbc.time_zone: UTC` stops the driver applying the JVM default time zone on the way in
and out.

## Entity equality

`equals` and `hashCode` use `publicId`, the immutable business key, not the surrogate id. A
generated id is null until flush, so an entity added to a `HashSet` before saving would be lost the
moment its hash changed. `publicId` is set at construction and marked `updatable = false`.

## Testing against real PostgreSQL

`CustomerRepositoryIT` runs against real PostgreSQL 16 from `compose.yaml`, reading
`SPRING_DATASOURCE_*` from the environment. H2 in PostgreSQL compatibility mode is not a
substitute: it differs on identity generation, `TIMESTAMPTZ` and constraint-violation behaviour,
so a green H2 run says nothing about the engine in production.

Testcontainers would be the better choice, because it gives every run a fresh database instead of
a shared one. It is unusable here: Docker Engine 29 removed the old REST API versions that the
Docker client bundled with Testcontainers still requests, so every discovery strategy receives
HTTP 400 from `/v1.32/info` while `/v1.44/info` answers 200 on the same socket. Worth recognising
the shape of that failure, because the error message blames the Docker environment and sends
people hunting for a socket path that was never wrong.

Context startup is itself an assertion, because `ddl-auto: validate` fails the whole suite if any
mapping and the migration disagree.
