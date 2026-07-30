# Lab 9 Guide - Northstar CRM Build Lab

## Overview
Turns the Lab 8 skeleton into a build-managed Maven project: full coordinates, Spring and JUnit dependencies with scopes, compiler/Surefire/jar plugins, and dev/test/prod profiles. Behavior stays stubbed. The deliverable is a trustworthy build.

## Compile / test / package / run
```
mvn clean package
java -jar target/customer-service.jar
```

## Design decisions
- JUnit is test scope so it never ships on the production classpath.
- Spring Context is a learning placeholder only, no Spring Boot code yet.
- finalName customer-service so the JAR is java -jar target/customer-service.jar.
- dev profile is active by default; prod is opted into with -Pprod.

## CI note (preview)
Preferred verify command on agents:

    mvn -B verify

-B is batch mode (non-interactive). Prefer verify over install on CI unless the pipeline intentionally publishes. Never deploy snapshots from a laptop without agreement.

Artifact coordinates: com.northstar:customer-service:0.1.0-SNAPSHOT
Sample customer IDs (docs only): CUS-1001, CUS-1002
Correlation ID (logs later): lab-request-001