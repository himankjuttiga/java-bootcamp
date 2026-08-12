package com.northstar.crm.event;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * Timed-path producer: publish a keyed CRM event with acks=all + idempotence.
 */
public class CustomerEventProducer {

  public static void main(String[] args) throws Exception {
    String bootstrap = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
    String topic = System.getenv().getOrDefault("CRM_EVENTS_TOPIC", "crm.customer-events.v1");

    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    // Durability: wait for all in-sync replicas before the write is considered committed.
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    // Idempotence: broker dedupes producer retries so Amina is not double-created.
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "lab30-customer-producer");

    String key = "CUS-1001";
    Path payloadPath = Path.of("events/customer-created-amina.json");
    String value = Files.readString(payloadPath);

    try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
      // Key = customerId so all of this customer's events land on the same partition (per-key ordering).
      ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
      RecordMetadata metadata = producer.send(record).get();
      System.out.printf(
          "Sent key=%s topic=%s partition=%d offset=%d timestamp=%d%n",
          key, metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());
    }
  }
}
