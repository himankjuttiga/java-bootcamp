package com.northstar.crm.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventListener {

  private static final Logger log = LoggerFactory.getLogger(CustomerEventListener.class);

  private final ProcessedEventStore store;
  private final List<CustomerEvent> handled = new CopyOnWriteArrayList<>();

  public CustomerEventListener(ProcessedEventStore store) {
    this.store = store;
  }

  @KafkaListener(topics = "${crm.kafka.customer-events-topic}")
  public void onCustomerEvent(
      @Payload CustomerEvent event,
      @Header(KafkaHeaders.RECEIVED_KEY) String key) {
    // Contract check: the Kafka key must equal the payload customerId (non-retryable -> DLT).
    if (key == null || !key.equals(event.customerId())) {
      throw new InvalidCustomerEventException(
          "key mismatch: key=" + key + " customerId=" + event.customerId());
    }
    // Idempotency: skip if this eventId was already processed (at-least-once replays).
    if (!store.markIfNew(event.eventId())) {
      log.info("duplicate_event_ignored id={} correlationId={}", event.eventId(), event.correlationId());
      return;
    }
    handled.add(event);
    log.info("customer_event_received id={} customerId={} correlationId={}",
        event.eventId(), event.customerId(), event.correlationId());
  }

  /** Events handled exactly once, exposed for tests / observability. */
  public List<CustomerEvent> handled() {
    return handled;
  }
}
