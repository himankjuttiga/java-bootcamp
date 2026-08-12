# Lab 31 — Spring Kafka Roles

## Reference

| Kafka idea | Spring Boot piece |
| --- | --- |
| Produce record | `KafkaTemplate.send(...)` |
| Consume record | `@KafkaListener` |
| Bootstrap servers | `spring.kafka.bootstrap-servers` |
| Group id | `spring.kafka.consumer.group-id` |

## Step 1 — Study table

Table copied above. `KafkaTemplate` is the Spring wrapper over the raw `KafkaProducer` from Lab 30; `@KafkaListener` is the wrapper over the raw `KafkaConsumer` + poll loop.

## Step 2 — CRM story

After the HTTP `POST /api/customers` creates Amina, the Customer service calls `kafkaTemplate.send("crm.customer-events.v1", "CUS-1001", envelopeJson)` — key `CUS-1001` so all of Amina's events land on the same partition — and returns to the caller without waiting on downstream consumers.

## Step 3 — Listener story

The notifications listener is annotated `@KafkaListener(topics="crm.customer-events.v1", groupId="crm-notifications")`, receives the record, deserializes the JSON envelope, reads `eventType` / `customerId` / `correlationId`, and sends the notification. Because it is its own group, it shares the partitions across its instances.

## Step 4 — Gap check (serializer question)

Should the producer/consumer use `StringSerializer` (envelope as raw JSON string, parse manually) or Spring's `JsonSerializer` / `JsonDeserializer` with a typed `CustomerEvent` DTO and trusted packages? Starting with String is simpler and matches the Lab 30 envelopes; typed JSON is cleaner but needs `spring.kafka.consumer.properties.spring.json.trusted.packages` configured.

## Predict / Debug

- **One app both producer and consumer?** Yes — a single Spring Boot app can hold a `KafkaTemplate` (publish) and `@KafkaListener` methods (consume) at the same time; the CRM service does exactly this.
- **KafkaTemplate only in a @RestController:** it couples publishing to the web layer and makes it unreusable/untestable from other entry points; put the publish in a service so any caller (REST, listener, scheduler) can emit events.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
