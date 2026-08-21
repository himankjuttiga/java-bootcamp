# Multi-stage sketch

1. Build stage: Maven + JDK 21 → `mvn -B -DskipTests package` to build the CRM JAR
2. Runtime stage: JRE 21 only + `COPY --from=build` the jar
3. USER 10001 (non-root; root fails the lab)
4. Not in final image: `.git`, `src`, `.env`, Maven
