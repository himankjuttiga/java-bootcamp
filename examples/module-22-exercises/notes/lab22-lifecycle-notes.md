# Lab 22 — Bean Lifecycle Callbacks

## Lifecycle order

Create (constructor) → Inject dependencies → `@PostConstruct` → In use → `@PreDestroy` (on context close). For `CustomerService`: log init once after wiring, log destroy when the context shuts down.

## @PostConstruct purpose

One-time setup that runs *after* all dependencies are injected and the bean is fully built — validating configuration, priming a cache, or logging that the service is ready. It is the safe place to touch injected collaborators, because they are guaranteed to be present by now.

## @PreDestroy purpose

One-time cleanup that runs when the container is shutting the bean down — releasing resources, flushing buffers, closing connections, and logging a clean shutdown. Proves the container, not your `main`, owns the shutdown.

## What not to do in init

Do not put per-request or business logic in `@PostConstruct`. It runs exactly once at startup, not per call, so creating `CUS-1001` there (or doing request work) is wrong. Keep it to one-time setup and logging.

## Singleton note

`CustomerService` is a singleton — one shared instance per context — so its `@PostConstruct` runs once, not per request or per injection.

## Debug / design challenge

No — Spring guarantees `@PostConstruct` runs *after* constructor injection completes. With constructor injection the dependencies are supplied during construction, and `@PostConstruct` is invoked only once the bean is fully initialized, so injected fields are never null inside it. (A caveat only arises if you tried to use field/setter-injected deps from within the constructor itself, which is a different mistake.)

## Predict the output / behavior

Exactly **one** `@PostConstruct` log for a singleton `CustomerService` per `@SpringBootTest` context. It is created once when that context starts. (Only if a test uses `@DirtiesContext` and forces a fresh context would you see it again.)

## Scope

Pre-lab only.
