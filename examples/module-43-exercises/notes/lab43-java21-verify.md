# JDK 21 verify

- `actions/setup-java@v4` with `distribution: temurin`, `java-version: "21"`. The CRM targets Java 21.
- `cache: maven`, `cache-dependency-path: examples/lab43-crm/pom.xml` so the cache key tracks the right pom.
- Command: `mvn -B -ntp clean verify`. No `-DskipTests`, no mvnw.
- Upload test reports on `if: always()`: `examples/lab43-crm/target/surefire-reports/**` as artifact `test-reports`, so a failing run still keeps its evidence.
