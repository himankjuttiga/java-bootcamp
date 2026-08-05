# Module 22 — Spring Core and Inversion of Control

**Central idea:** Inversion of Control (IoC) means your objects no longer create, find, or manage their own dependencies. The framework does it for them. You surrender control of *construction and wiring* to the container, and in exchange you gain loose coupling, testability, swappable implementations, and centralized configuration. Everything else in this module is a consequence of that one shift.

---

## 1. Inversion of Control — the principle

In ordinary code, your class is in charge: it decides what to build, when, and with which concrete types. IoC turns that relationship around — the framework is in charge of the object graph, and it calls into your code rather than your code calling out to construct collaborators. This is often summarized as the **Hollywood Principle: "Don't call us, we'll call you."**

The practical payoff is that the *decision* about which implementation to use moves out of the class and into configuration the container reads. The class stops caring whether its collaborator is an in-memory stub, a JDBC-backed repository, or a mock in a test.

## 2. Dependency Injection — the technique

IoC is the *goal*; Dependency Injection (DI) is the specific mechanism Spring uses to reach it. DI means a class receives its collaborators from the outside instead of building them internally. IoC is the *why*, DI is the *how* — people often use the terms interchangeably, but IoC is the broader concept and DI is one way to implement it.

**Before (tight coupling):**

```java
public class CustomerService {
    private final CustomerRepository repository = new InMemoryCustomerRepository(); // hard-wired
}
```

This class is welded to one concrete type. You cannot swap in a database implementation or a test double without editing the class itself, and it is hard to test in isolation.

**After (dependency injected):**

```java
public class CustomerService {
    private final CustomerRepository repository;
    public CustomerService(CustomerRepository repository) { // handed in from outside
        this.repository = repository;
    }
}
```

Now the service depends only on the `CustomerRepository` *interface*. The container decides which concrete bean to inject, and the same class works unchanged across production, test, and future implementations.

## 3. The Dependency Inversion Principle (the "why" behind DI)

DI is the runtime expression of the **Dependency Inversion Principle** (the "D" in SOLID): high-level modules and low-level modules should both depend on **abstractions**, not on each other's concrete details. `CustomerService` (high-level policy) depends on the `CustomerRepository` interface (abstraction), and `InMemoryCustomerRepository` (low-level detail) also depends on that same interface by implementing it. Both point at the abstraction in the middle, which is what makes the pieces interchangeable.

## 4. The Spring IoC container

The container is the engine that reads your configuration, instantiates the objects it manages, injects their dependencies, and governs their lifecycle.

- **`BeanFactory`** — the minimal, low-level container interface. Lazy, lightweight, rarely used directly.
- **`ApplicationContext`** — the superset used by virtually all real applications. It adds event publishing, internationalization, resource loading, annotation processing, and automatic bean post-processing on top of `BeanFactory`.

Common `ApplicationContext` implementations include `AnnotationConfigApplicationContext` (Java/annotation config) and the web variants; in Spring Boot the context is created and configured for you when the app starts.

## 5. Beans

A **bean** is simply an object that the Spring container instantiates, assembles, and manages. "Bean" is not a special kind of class — it is any object whose lifecycle Spring owns. When you ask the container for a dependency, it hands you a bean it created and wired.

## 6. Three ways to define beans

**a) Stereotype annotations + component scanning.** Mark a class and let Spring auto-detect it during a classpath scan. The stereotypes are semantic specializations of `@Component`:

| Annotation | Intended role |
| --- | --- |
| `@Component` | Generic Spring-managed component |
| `@Service` | Business/service-layer logic |
| `@Repository` | Data access; also translates persistence exceptions |
| `@Controller` / `@RestController` | Web request handlers |
| `@Configuration` | Class that declares `@Bean` methods |

Your CRM already uses this style — `@Service CustomerService`, `@Repository InMemoryCustomerRepository`, `@RestController CustomerController`, `@Component CorrelationFilter` / `CustomerMetrics`.

**b) Java configuration with `@Bean` methods.** An explicit factory approach, ideal for wiring third-party classes you cannot annotate:

```java
@Configuration
public class CrmConfig {
    @Bean
    public CustomerRepository customerRepository() {
        return new InMemoryCustomerRepository();
    }
    @Bean
    public CustomerService customerService(CustomerRepository repo, CustomerMetrics metrics) {
        return new CustomerService(repo, metrics); // Spring passes the beans as method args
    }
}
```

**c) XML configuration.** The original style (`<bean>` definitions). Still supported, but legacy — new projects use annotations or Java config.

## 7. Injecting dependencies

**Constructor injection — preferred.** Dependencies can be `final`, the object is never in a half-built state, required collaborators are explicit, and testing is trivial (just call the constructor). With a single constructor, Spring 4.3+ injects automatically with no `@Autowired` needed:

```java
@Service
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerMetrics metrics;
    public CustomerService(CustomerRepository repository, CustomerMetrics metrics) {
        this.repository = repository;
        this.metrics = metrics;
    }
}
```

**Setter injection** — for genuinely optional dependencies that can change after construction.

**Field injection** (`@Autowired` directly on a field) — works, but discouraged: dependencies become invisible in the API, fields cannot be `final`, and the class is hard to instantiate in a plain unit test without reflection.

## 8. Resolving ambiguity

When more than one bean satisfies a required type, the container needs a tie-breaker:

- **`@Primary`** — marks one bean as the default choice when several candidates match.
- **`@Qualifier("beanName")`** — names the exact bean to inject at the injection point.
- **`@Autowired(required = false)`** or an `Optional<T>` parameter — tolerate a dependency that might not be present.

## 9. Bean scopes

The scope controls how many instances the container creates and how long they live:

| Scope | Meaning |
| --- | --- |
| `singleton` (default) | One shared instance per container, reused everywhere |
| `prototype` | A brand-new instance every time the bean is requested |
| `request` (web) | One instance per HTTP request |
| `session` (web) | One instance per HTTP session |

Because singletons are shared across threads, they must be **stateless and thread-safe** — hold no per-request mutable state. Your services and repositories are singletons precisely because they carry no conversational state.

## 10. Bean lifecycle

The container walks each bean through a defined sequence:

1. **Instantiate** — construct the object.
2. **Populate** — inject its dependencies.
3. **Initialize** — run setup callbacks such as `@PostConstruct` (or `InitializingBean.afterPropertiesSet`), after wiring is complete.
4. **In use** — the bean serves the application.
5. **Destroy** — on shutdown, run cleanup callbacks such as `@PreDestroy` (or `DisposableBean.destroy`).

These hooks are the right place to acquire resources once dependencies are ready and to release them cleanly at shutdown.

## 11. Profiles and environment-specific wiring

`@Profile` lets you register different beans for different environments, activated via `spring.profiles.active`. A classic use is an in-memory repository for `dev`/`test` and a real database-backed repository for `prod`:

```java
@Repository
@Profile("dev")
public class InMemoryCustomerRepository implements CustomerRepository { ... }

@Repository
@Profile("prod")
public class JdbcCustomerRepository implements CustomerRepository { ... }
```

The service code never changes; only the active profile decides which bean the container wires in. Externalized configuration (`application.yml`, `@Value`, `@ConfigurationProperties`, `Environment`) complements this by keeping settings out of code.

## 12. Aspect-Oriented Programming (AOP) in Spring Core

AOP handles **cross-cutting concerns** — behavior many classes need but that belongs to none of them: logging, transactions, security, metrics timing, caching. Rather than copying that plumbing into every method, you write it once as an **aspect** and declare where it applies, and Spring weaves it in using the same container via **runtime proxies**.

Vocabulary:

- **Aspect** — the module bundling the cross-cutting logic (e.g. a `LoggingAspect`).
- **Advice** — the action plus its timing: `@Before`, `@After`, `@AfterReturning`, `@AfterThrowing`, `@Around`.
- **Join point** — a point in execution where advice could run (in Spring, a method invocation).
- **Pointcut** — an expression selecting which join points match (e.g. all methods in the service package).

You have already used AOP without naming it: `@Transactional`, `@Timed`, and Module 21's `@NewSpan` all work by Spring wrapping your bean in a proxy and running advice around your real method. Because Spring AOP is proxy-based, it applies to container-managed beans (and, for the default proxies, public methods called from outside the bean).

## 13. Why it matters for the CRM (Lab 22 preview)

Lab 22 replaces the remaining hand-written `new` wiring across the CRM object graph with Spring-managed beans and constructor injection, so the container assembles the whole graph and every implementation becomes swappable. Concretely: depend on the `CustomerRepository` interface rather than `new InMemoryCustomerRepository()`, let component scanning or `@Bean` methods define the beans, use constructor injection throughout, and lean on profiles to vary implementations per environment — turning the app from a pile of self-constructing objects into a configurable, testable, container-managed graph.

---

## Quick revision checklist

- IoC = framework controls object creation and wiring; DI = the injection technique that achieves it.
- Depend on interfaces (abstractions), never on concrete classes.
- `ApplicationContext` is the container; beans are the objects it manages.
- Define beans via stereotypes + scanning, `@Configuration`/`@Bean`, or (legacy) XML.
- Prefer constructor injection; use `@Primary` / `@Qualifier` to resolve ambiguity.
- Default scope is singleton — keep singletons stateless and thread-safe.
- Lifecycle hooks: `@PostConstruct` for setup, `@PreDestroy` for cleanup.
- `@Profile` swaps beans per environment with no code change.
- AOP weaves cross-cutting concerns via proxies using aspects, advice, join points, and pointcuts.
