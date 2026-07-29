# Northstar CRM - Lab 8 Skeleton

Maven Java skeleton for the Customer Management Platform. Structure only, no Spring, JPA, or HTTP yet.

## Build and run

```
mvn clean compile
java -cp target/classes com.northstar.crm.Main
```

Expected output:

```
Northstar CRM skeleton — Lab 8
Packages: controller, service, repository, entity, dto, config, exception
Examples: CUS-1001 Amina Khan ACTIVE | CUS-1002 Ravi Singh PROSPECT
```

## Layout
Seven layer packages under com.northstar.crm: controller, service, repository, entity, dto, config, exception, plus Main. See docs/CODING-STANDARDS.md and docs/layer-flow.md.

## Build and CI

```
mvn clean package
java -jar target/customer-service.jar
```

Teammates and CI should reproduce the build with `mvn -B verify`.
Artifact: com.northstar:customer-service:0.1.0-SNAPSHOT