# Lab 31 — Fill Spring Kafka TODOs

## Step 1 — Snippet (filled)

```java
// application.yml ideas
spring.kafka.bootstrap-servers: localhost:9092   // override via ${KAFKA_BOOTSTRAP_SERVERS}
spring.kafka.consumer.group-id: crm-notifications

@Service
class CustomerEventPublisher {
  private final KafkaTemplate<String, String> template;

  void publishCreated(String customerId, String json) {
    // key MUST be the customerId (CUS-1001 / CUS-1002), NOT a random UUID,
    // so all of one customer's events keep per-partition order.
    template.send("crm.customer-events.v1", customerId, json); // topic
  }
}

@KafkaListener(topics = "crm.customer-events.v1", groupId = "crm-notifications")
void onEvent(String payload) {
  // TODO: parse envelope + idempotent handle (dedupe on eventId before side effect)
  // TODO Lab 31: route poison messages to crm.customer-events.v1.dlq
}
```

## Step 2 — Filled blanks

- bootstrap-servers -> `localhost:9092` (or instructor bootstrap)
- consumer.group-id -> `crm-notifications`
- publish topic -> `crm.customer-events.v1`
- listener topic -> `crm.customer-events.v1`

## Step 3 — Key reminder

The `send(...)` key argument must be `CUS-1001` / `CUS-1002` (the `customerId`), never a random UUID, or per-customer ordering breaks.

## Step 4 — DLT blank

`// TODO Lab 31: route poison messages to crm.customer-events.v1.dlq`

## Predict / Debug

- **JsonSerializer without trusted packages:** the consumer's `JsonDeserializer` throws an "not in trusted packages" error and cannot build the DTO, so consume fails until `spring.kafka.consumer.properties.spring.json.trusted.packages` is set (or you use String + manual parse).
- **Publish without key:** null key -> round-robin across partitions -> a customer's events scatter and lose ordering.

## Config hygiene

Externalize broker and topic names in `application.yml` with env overrides; never hard-code secrets. Dependency: `spring-kafka` (+ `spring-kafka-test` for `@EmbeddedKafka`). Notifications and audit must use **distinct** group ids.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
