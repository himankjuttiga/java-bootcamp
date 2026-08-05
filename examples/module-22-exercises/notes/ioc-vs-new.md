# Lab 22 — IoC Versus Manual Wiring

| Approach | Who creates collaborators? | Test impact |
| --- | --- | --- |
| Manual `new` | The service constructs its own `InMemoryCustomerRepository` internally | Hard to test — you cannot swap in a fake, so tests hit the real store and you'd have to edit production code to isolate |
| IoC / DI | Spring (in production) or the test (in a unit test) supplies the collaborator from outside | Easy — the constructor takes a `CustomerRepository`, so a test passes a fake or mock with no Spring required |

## Smell (one sentence)

`CustomerService` owns the construction of its own dependency with `new InMemoryCustomerRepository()`, welding it to one concrete implementation.

## Fix (one sentence)

Declare `CustomerRepository` as a constructor parameter and let the container (or a test) supply the concrete collaborator, so the service depends on the abstraction, not a specific store.

## Debug / design challenge

A service that does `new NotificationService()` can't be verified without real notifications firing. Rewrite it to accept the collaborator:

```java
public class OrderService {
    private final NotificationService notifications;
    public OrderService(NotificationService notifications) {
        this.notifications = notifications;
    }
    public void place(Order order) { notifications.notify(order); }
}
```

Now a plain unit test needs no Spring:

```java
NotificationService fake = mock(NotificationService.class);
new OrderService(fake).place(order);
verify(fake).notify(order);   // assert notify was called
```

## Predict the output / behavior

Two places each calling `new` on the repository produce **two separate in-memory maps** at runtime — two independent instances, so data written through one is invisible to the other. Under IoC the singleton bean is shared, giving exactly one map. This is precisely the bug manual wiring invites.

## Scope

Pre-lab only.
