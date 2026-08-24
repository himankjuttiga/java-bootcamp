-- Lab 40 — V2: the column object-level authorisation is decided on.
--
-- V1 is already applied everywhere, so this arrives as a new migration rather than an edit,
-- exactly as the Lab 39 notes said it would have to.
--
-- Backfill then NOT NULL, in that order: an existing row has no owner, and a NOT NULL column
-- added in one statement would fail on a non-empty table.

ALTER TABLE customer ADD COLUMN owner_agent VARCHAR(64);

UPDATE customer SET owner_agent = 'agent-a' WHERE owner_agent IS NULL;

ALTER TABLE customer ALTER COLUMN owner_agent SET NOT NULL;

-- Every list read is now filtered by owner first, so the owner column leads the index.
-- This replaces ix_customer_status_created for that access path; the old index stays for
-- admin-style reporting that is not scoped to one agent.
CREATE INDEX ix_customer_owner_status
  ON customer (owner_agent, status, created_at DESC, customer_id DESC);
