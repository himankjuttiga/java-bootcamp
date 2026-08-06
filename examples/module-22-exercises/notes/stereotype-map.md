# Lab 22 — Stereotype Annotation Map

| Class | Stereotype |
| --- | --- |
| CustomerController | @RestController |
| CustomerService | @Service |
| NotificationService | @Service |
| InMemoryCustomerRepository | @Repository |
| Customer (model) | none — plain type, not a Spring bean |

## Debug / design challenge

No — the `CustomerRepository` *interface* should not get `@Repository`. Spring instantiates concrete classes, not interfaces, so the annotation belongs on the implementation (`InMemoryCustomerRepository`). The service depends on the interface; the container supplies the annotated concrete bean.

## Predict the output / behavior

If `InMemoryCustomerRepository` has neither `@Repository` (nor any stereotype) nor an `@Bean` definition, component scanning never registers it. A constructor that needs a `CustomerRepository` then fails at startup with `NoSuchBeanDefinitionException` (wrapped in `UnsatisfiedDependencyException`), and the context refuses to start.

## Scope

Pre-lab only.
