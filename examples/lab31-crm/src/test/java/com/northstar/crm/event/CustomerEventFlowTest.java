package com.northstar.crm.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(partitions = 3, topics = {"crm.customer-events.v1"})
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.consumer.auto-offset-reset=earliest"
})
class CustomerEventFlowTest {

  @Autowired
  CustomerEventPublisher publisher;

  @Autowired
  CustomerEventListener listener;

  private static CustomerEvent created(String eventId, String customerId, String name, String status) {
    return new CustomerEvent(
        eventId, "CustomerCreated", 1,
        Instant.parse("2026-07-13T06:00:00Z"),
        customerId, "lab-request-001", "customer-service",
        new CustomerEvent.CustomerData(name, status));
  }

  @Test
  void publishesAndConsumesCustomerCreated() {
    CustomerEvent amina = created("evt-amina-1", "CUS-1001", "Amina Khan", "ACTIVE");
    publisher.publish(amina);

    await().atMost(Duration.ofSeconds(20)).untilAsserted(() ->
        assertThat(listener.handled())
            .extracting(CustomerEvent::eventId)
            .contains("evt-amina-1"));
  }

  @Test
  void duplicateEventIgnored() {
    CustomerEvent ravi = created("evt-ravi-1", "CUS-1002", "Ravi Singh", "PROSPECT");
    // Publish the same event twice -> at-least-once replay simulation.
    publisher.publish(ravi);
    publisher.publish(ravi);

    await().atMost(Duration.ofSeconds(20)).untilAsserted(() ->
        assertThat(listener.handled())
            .extracting(CustomerEvent::eventId)
            .contains("evt-ravi-1"));

    // Idempotency: even after two publishes, the event is handled exactly once.
    long timesHandled = listener.handled().stream()
        .filter(e -> e.eventId().equals("evt-ravi-1"))
        .count();
    assertThat(timesHandled).isEqualTo(1L);
  }
}
