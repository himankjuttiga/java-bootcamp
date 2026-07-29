# Lab 10 Review-Log TODOs - Module 10 Exercise 4

Prompt strength: Strong (goal + context + constraints + fixtures)
Phantom annotation found? Yes - @Builder (Lombok, not a declared dependency)
Fixture check Amina status: ACTIVE
Fixture check Ravi status: PROSPECT
JDK/Maven note: java -version reads 21.x; mvn -version Java home points at JDK 21
Accept / Reject / Edit: Edit

## Reject/Edit reason

Copilot added a Lombok @Builder annotation, which is an invented dependency we never declared, so I removed it and kept plain Java 21 with a normal constructor.

## Self-check

Amina is ACTIVE, Ravi is PROSPECT. No swapped statuses.
