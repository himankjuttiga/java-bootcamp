# Lab 8 Answers - Project Structure

## Layer table

| Layer | Package | Owns | Must NOT own |
| ----- | ------- | ---- | ------------ |
| Presentation | controller | Accept/return DTOs, map calls | SQL, business rules |
| Business | service | Rules, orchestration | HTTP headers, JDBC |
| Persistence | repository | Save/find | REST mapping |
| Domain | entity | Customer fields | Request JSON shapes |
| Contracts | dto | Request/response | Persistence annotations |
| Cross-cutting | config, exception | Wiring, failure types | Happy-path create logic |

## Concepts

1. Once implemented, a create request flows controller -> service -> repository and a response DTO comes back out.
2. The trust boundary is the controller. Input validation will live there (and in the service) later.
3. Happy path returns a CustomerResponse. Failure like a missing customer throws CustomerNotFoundException later.
4. CUS-1001 is the stable identity that never changes. Amina Khan is just the display name and can change.
5. save/findById at the repository need to be idempotent later so retries don't create duplicates.
6. Local dev can use an in-memory list. Production needs PostgreSQL for durability.
7. Once APIs exist we'll want request logs with the correlation ID (lab-request-001), latency metrics, and error counts.
8. Two instances sharing customer IDs need a shared store, or the same ID could point at different data.
9. Entity must not import controller because the domain should not depend on transport. It also creates a cycle.
10. dto holds the fields the caller sends and receives. entity holds internal state like the generated ID and status.

## Checkpoints

### A - Project root + layout
1. pom.xml with com.northstar:customer-service:0.1.0-SNAPSHOT - Pass
2. Standard src/main/java, src/main/resources, src/test/java - Pass
3. Seven packages under com.northstar.crm - Pass
4. Edited in IntelliJ - Pass

### B - Stubs compile, Main runs
1. All stub classes + Main present - Pass
2. clean compile -> BUILD SUCCESS - Pass
3. Main prints the banner and example IDs - Pass
4. No Spring/JPA/Kafka imports - Pass

### C - Documentation
1. layer-flow.md narrates CUS-1001 / lab-request-001 - Pass
2. CODING-STANDARDS.md states hard layer rules - Pass
3. LAB-8-GUIDE.md explains compile/run - Pass

### D - Failure evidence + security
1. Three failure experiments recorded - Pass
2. Layer-direction violation understood and reverted - Pass
3. No secrets / target committed - Pass

## Failure experiments

1. Renamed pom.xml to pom.bak and tried to build. Maven could not find the POM and the build failed. Restored the name and it built again.
2. Called new CustomerRepository().findById("CUS-1001") from a throwaway main. It threw UnsupportedOperationException, which is the expected Lab 8 stub behavior. Removed the throwaway code.
3. Added import com.northstar.crm.controller.CustomerController inside CustomerRepository. It still compiled, but it breaks the layer rule (persistence depending on presentation), so a reviewer would reject it. Removed the import right away.

## Reflection

1. Keeping DTOs separate from the entity mattered most, since mixing them causes leaks later.
2. The hardest bug was the DTO file that held the wrong class name, which cascaded into service and controller errors.
3. A clean mvn compile plus the find listing of packages proves the structure is real, not just planned.
4. At ten times the team size, messy packages cause constant merge conflicts and nobody knows where code goes.
5. Cross-cutting concerns like logging and security move to shared config later.
6. Before real data, we need validation, persistence, and secrets kept out of source.
7. Lab 9 expands the POM, Labs 10-12 fill domain and service code, later labs add Spring and a database.
8. Once APIs exist, request latency and the correlation ID in logs matter most.
9. Separate DTOs stop API fields from leaking into storage and vice versa for CUS-1001.
10. When Spring arrives the packages stay stable, but the stub classes get annotations and real bodies first.

## Security review

1. Untrusted inputs are future API request payloads.
2. Auth, authorization, and validation will be enforced at the controller and service.
3. No sensitive values in Lab 8, and it should stay that way.
4. mvn compile is safe to retry. Create customer is not implemented yet.
5. After a partial failure the stub throws before storing anything.
6. Operators will later monitor API latency and database health.
7. Empty stubs and no auth are fine locally but unacceptable in production.
8. Contracts get versioned later with WSDL/OpenAPI.