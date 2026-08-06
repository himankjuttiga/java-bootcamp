# Lab 22 — Constructor Injection Preference

## Preferred pattern

Constructor injection with `final` fields. Required collaborators are passed in through the constructor and can never be reassigned:

```java
@Service
public class CustomerService {
    private final CustomerRepository repository;
    private final NotificationService notifier;
    public CustomerService(CustomerRepository repository, NotificationService notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }
}
```

With a single constructor, Spring injects automatically — no `@Autowired` needed. The dependencies are explicit, immutable after construction, and the object is never in a half-built state.

## Why (testability)

The constructor is the public contract, so a unit test builds the object directly with fakes and needs no Spring context: `new CustomerService(fakeRepo, fakeNotifier)`. Every required dependency is visible in the signature, so reviewers and tests can see exactly what the class needs.

## Avoid

Field injection (`@Autowired` on a private field) as the primary pattern. It hides dependencies from the constructor/API, prevents `final`, and forces reflection or a running container to instantiate the class in tests.

## Setter role (one line)

Setter injection is for genuinely optional dependencies that may be absent or changed after construction — not the primary wiring path for Lab 22.

## Debug / design challenge

No — a field injected only via `@Autowired` cannot be `final`, because the field is set by reflection *after* the object is constructed, whereas `final` requires assignment during construction. This is one of the concrete reasons constructor injection is preferred.

## Predict the output / behavior

If a constructor dependency has no matching bean, the container fails **fast at startup** — `ApplicationContext` refuses to start with a `NoSuchBeanDefinitionException` / `UnsatisfiedDependencyException`. That is desirable: the problem surfaces immediately at boot rather than as a `NullPointerException` deep in a request later, which is what field injection tends to defer.

## Scope

Pre-lab only.
