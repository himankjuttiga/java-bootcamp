# Lifecycle Notes - Module 9 Exercise 3

Mapping each Maven phase to what it proves for Northstar CRM.

## Step 1 / 2 - Command for each intent

| Intent | Command |
| ------ | ------- |
| Confirm POM parses before coding further | mvn validate |
| Compile production Java only | mvn compile |
| Run unit tests | mvn test |
| Produce target/customer-service.jar | mvn package |
| Run package plus CI verification checks | mvn verify (often mvn -B verify) |
| Put the JAR into the local .m2 cache | mvn install |

## Phase meanings

| Phase | What it proves |
| ----- | -------------- |
| validate | POM/model is structurally OK |
| compile | Production sources compile to target/classes |
| test | Unit tests pass |
| package | Artifact (JAR) exists under target/ |
| verify | Extra checks/integrations succeed |
| install | Artifact copied into local ~/.m2 |
| deploy | Published to a remote repo (CI/release only) |

## Step 3 - Correct order (no deploy)

1. validate
2. compile
3. test
4. package
5. verify
6. install

## Step 4 - Why CI prefers verify

Continuous Integration usually runs mvn -B verify so the build is batch/non-interactive and stops after verification, without casually installing or deploying from every laptop. deploy belongs to credentialed release/CI publishing, not classroom machines.