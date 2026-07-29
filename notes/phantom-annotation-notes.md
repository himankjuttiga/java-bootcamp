# Phantom Annotation Notes - Module 10 Exercise 3

Flagging Copilot-style annotations that do not belong in a plain Java 21 prep sketch.

## Step 1 - Table (with one trap row)

| Seen in suggestion | Likely real? | Prep action |
| ------------------ | ------------ | ----------- |
| @Entity / @Table | JPA only | Defer - not Lab 10 scope |
| @Service / @Autowired | Spring | Defer - hosting labs later |
| @NotNull (Jakarta) | Validation lib | Name it, do not invent the import |
| public record Customer(...) | Java 16+ | OK on JDK 21 |
| @Builder (Lombok) | Lombok, not added | Trap - reject, Lombok is not a dependency |

## Step 2 - Reject rule

Reject any import I cannot name from JDK 21 or an agreed Maven dependency.

## Step 3 - Fixture check

If a suggestion hard-codes CUS-1002 Ravi as ACTIVE, that is a review fail. Ravi's correct status is PROSPECT.

## Step 4 - Out of scope

SOAP and Spring Boot hosting are not part of this pre-lab. They come later, before Labs 13 and 24.
