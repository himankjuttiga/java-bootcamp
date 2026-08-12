# Lab 30 — Fill Kafka Basics TODOs

## Step 1 — Copy the quiz (filled)

1. A **topic** is a named stream of records.
2. A **partition** is an ordered subset of a topic; offsets are per partition.
3. The **offset** is the consumer's position in a partition.
4. Consumers in the same **consumer group** compete for partitions; different groups each get a copy.

## Step 2 — Fill blanks

topic / partition / offset / consumer group — one each, as above.

## Step 3 — CRM example

Group `crm-notifications` shares the 3 partitions across its members (competing consumers, load-balanced); group `crm-audit` is a separate group that independently reads all `CUS-1001` / `CUS-1002` events end to end for its own audit trail.

## Step 4 — Self-check

Matches the vocabulary: topic (channel), partition (ordered log slice), offset (per-partition position, tracked per group), consumer group (load-sharing unit).

## Predict / Debug

- **Is offset a global message id?** No — the offset is a position *within one partition*, not a global id across the topic, and it is tracked per consumer group.
- **Replica count 1 (lab) vs prod:** the lab uses RF=1 (single-node broker) so there is no redundancy — a broker loss loses data; production uses RF=3 so the partition survives a broker failure via an in-sync replica.

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
