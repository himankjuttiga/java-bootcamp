# Dockerfile TODOs

```dockerfile
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre AS runtime
USER 10001
COPY --from=build /workspace/target/*.jar /app/app.jar
HEALTHCHECK CMD wget -qO- http://localhost:8080/actuator/health/readiness || exit 1
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

Pom-first COPY keeps the layer cache warm. No secret ARG/ENV — password injected at run time.
