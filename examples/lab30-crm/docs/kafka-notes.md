# Lab 30 — Kafka notes (timed path)

## Produce -> consume

The `CustomerEventProducer` (or console producer) sends a record keyed by `customerId` to `crm.customer-events.v1`; the KRaft broker appends it to one of the topic's 3 partitions and assigns it an offset. A console consumer with `--from-beginning` then reads the record back, printing key, partition, offset, and timestamp, proving the event survived the broker and can be replayed independently of the producer.

## Keying

Key = `customerId` (e.g. `CUS-1001`, `CUS-1002`). Kafka hashes the key to pick a partition, so every event for one customer lands on the same partition, and because order is guaranteed within a partition, that customer's events stay in the sequence they occurred. A null or random key would round-robin events across partitions and lose per-customer ordering.

## DLQ

`crm.customer-events.v1.dlq` (1 partition) is the dead-letter topic. Poison records (unparseable / schema-invalid) and records that keep failing after retries are routed here in Lab 31 so the main consumer group keeps making progress instead of being blocked by one bad message.

## Frozen contract (Lab 31 hand-off)

| Item | Lab value |
| ---- | --------- |
| Bootstrap (host) | `localhost:9092` |
| Bootstrap (in Compose network) | `kafka:9092` |
| Primary topic | `crm.customer-events.v1` (3 partitions) |
| DLQ topic | `crm.customer-events.v1.dlq` (1 partition) |
| Record key | `customerId` (`CUS-1001`, `CUS-1002`) |
| Sample correlation | `lab-request-001` |
| Demo groups | `crm-notifications` (competing), `crm-audit` (independent) |

## Runbook (commands a peer must run)

```bash
docker compose up -d
docker compose ps

# create topics
docker exec crm-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 \
  --create --topic crm.customer-events.v1 --partitions 3 --replication-factor 1
docker exec crm-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 \
  --create --topic crm.customer-events.v1.dlq --partitions 1 --replication-factor 1
docker exec crm-kafka /opt/kafka/bin/kafka-topics.sh --bootstrap-server localhost:9092 \
  --describe --topic crm.customer-events.v1

# produce (CLI, keyed)
docker exec -it crm-kafka /opt/kafka/bin/kafka-console-producer.sh \
  --bootstrap-server localhost:9092 --topic crm.customer-events.v1 \
  --property parse.key=true --property key.separator=:

# consume with metadata
docker exec crm-kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 --topic crm.customer-events.v1 \
  --from-beginning --property print.key=true --property print.partition=true \
  --property print.offset=true --property print.timestamp=true --max-messages 3

# lag describe
docker exec crm-kafka /opt/kafka/bin/kafka-consumer-groups.sh \
  --bootstrap-server localhost:9092 --describe --group crm-notifications

# Java producer
mvn -B -q package
mvn -B exec:java -Dexec.mainClass=com.northstar.crm.event.CustomerEventProducer
```

## Ordering and delivery semantics (for Lab 31)

1. **Per-key ordering** — same `customerId` -> same partition -> relative order preserved for that customer.
2. **No global order** — events for `CUS-1001` and `CUS-1002` may interleave across partitions.
3. **At-least-once** — consumers may see duplicates after rebalance/retry; Lab 31 must be idempotent on `eventId`.
4. **DLQ purpose** — poison / repeatedly failing records move aside so the main group keeps progressing.

## Production checklist (lab-only vs prod)

- **Security:** lab uses PLAINTEXT on the Docker network only; production needs TLS + SASL auth for publish/consume.
- **Replication:** lab uses RF=1 (single node, no redundancy); production uses RF=3 with `min.insync.replicas=2`.
- **Topic creation:** create topics explicitly; disable auto-create so a typo cannot invent a stray production stream.
- **Secrets/PII:** fictional PII only; never put secrets in event `data`.
