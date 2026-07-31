# Lab 9 Answers - Maven Build and Dependencies

## Concepts

1. The flow here is source -> compile -> test -> package -> optional install, not a customer request.
2. The trust boundary is between artifacts downloaded from Maven Central and my own source code.
3. Each lifecycle phase either succeeds or fails, and a failure stops the phases after it.
4. The artifact identity is groupId:artifactId:version, which is separate from customer IDs like CUS-1001.
5. mvn install is safe to repeat; it overwrites the snapshot in the local .m2.
6. The dev profile is the local shortcut; prod is the real production config, opted into deliberately.
7. When a CI build fails I need the failing phase, the error, and the test/surefire report.
8. Two instances built from the same POM version produce the same artifact, so they stay consistent.
9. test scope keeps JUnit off the runtime classpath, so it is never shipped with the app.
10. CI prefers verify so it proves the build without writing snapshots into every shared agent's .m2.

## Checkpoints

### A - Copy + coordinates
1. lab9-crm exists (copied from Lab 8) - Pass
2. pom.xml has com.northstar:customer-service:0.1.0-SNAPSHOT, packaging jar - Pass
3. maven.compiler.release 21 - Pass
4. Edited in IntelliJ - Pass

### B - Dependencies, plugins, tests
1. Spring placeholder + JUnit test scope declared - Pass
2. PlaceholderTest passes under Surefire - Pass
3. Compiler + jar Main-Class configured - Pass
4. mvn test and mvn package succeed - Pass

### C - Lifecycle + tree + profiles
1. lifecycle-evidence.md covers validate to install - Pass
2. dependency-tree.txt annotated - Pass
3. Profiles dev/test/prod demonstrated - Pass
4. application-dev.properties has no secrets - Pass

### D - JAR, CI, failures, security
1. java -jar target/customer-service.jar works - Pass
2. README documents mvn -B verify - Pass
3. Three failure experiments recorded - Pass
4. No secrets / target / .m2 committed - Pass

## Failure experiments

1. Set spring.version to a nonsense value and ran compile. Maven could not resolve the artifact and the build failed. Restored the real version.
2. Changed PlaceholderTest to assertTrue(false) and ran test. The test failed and so did verify. Restored the assertion.
3. Ran install twice. Both succeeded; the second overwrote the snapshot in .m2, which shows snapshot installs are idempotent.

## Reflection

1. Correct dependency scopes mattered most, since a wrong scope ships the wrong libraries.
2. The hardest issue was the JDK 26 vs 21 problem breaking the test framework until I switched to 21.
3. Running each phase separately and recording it in lifecycle-evidence.md proves the walk was real.
4. At ten times the dependencies, transitive conflicts and slow resolves break first.
5. An artifact repository and CI cache should move to shared infrastructure.
6. Before real data we need validation, a database, and secrets kept out of the POM.
7. Lab 8 gave the structure, Lab 9 builds it, and Lab 10+ fills the domain code.
8. When verify fails, the surefire report and the failing phase matter most.
9. test scope is more than style because it keeps test libraries out of the shipped app.
10. When Spring Boot arrives the coordinates and layout stay stable; the dependencies and Main change first.