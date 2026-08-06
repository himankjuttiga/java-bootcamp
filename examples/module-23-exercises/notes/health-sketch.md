# Lab 23 — CrmApplication Stub (TODOs)

## Main class annotation

`@SpringBootApplication` on `CrmApplication` (it bundles `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`).

## run(...) line

`SpringApplication.run(CrmApplication.class, args);`

```java
@SpringBootApplication
public class CrmApplication {
  public static void main(String[] args) {
    SpringApplication.run(CrmApplication.class, args);
  }
}
```

## Health URL

`GET http://localhost:8080/actuator/health` → expect `{"status":"UP"}`

## Package root

`com.northstar.crm` — the app class sits at the root so component scanning reaches all sub-packages.

## Debug / design challenge

If `CrmApplication` sits in `com.demo` instead of `com.northstar.crm`, component scanning is rooted at `com.demo` and never reaches the CRM beans in `com.northstar.crm.*`. The controller/service/repository are not registered, so constructor injection fails at startup with `NoSuchBeanDefinitionException` and the context refuses to start.

## Predict the output / behavior

No — `@SpringBootApplication` does not replace stereotypes on services. It enables component scanning, but a class is only discovered if it carries a stereotype (`@Service`, `@Repository`, `@RestController`, `@Component`) or is declared via an `@Bean` method. Scanning finds annotated classes; it does not annotate them for you.

## Scope

Pre-lab only.
