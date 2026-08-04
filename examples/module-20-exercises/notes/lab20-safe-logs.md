# Lab 20 — Rewrite Unsafe Logs

## Unsafe example

`log.info("created {}", customer);` — Customer.toString() includes name, email, and phone, so the line becomes `created Customer{name=Amina Khan, email=amina@example.com, phone=+1-555-0100, status=ACTIVE}`. Any name/email/phone in a log is a reportable privacy incident.

## Safe rewrite (Amina/CUS-1001)

`log.info("event=customer.created customerId={} status={} correlation={}", "CUS-1001", "ACTIVE", "lab-request-001");`

Renders as: `event=customer.created customerId=CUS-1001 status=ACTIVE correlation=lab-request-001`

## Safe Ravi activate start

`log.info("event=customer.activation.start customerId={} status={} correlation={}", "CUS-1002", "PROSPECT", "lab-request-001");`

Renders as: `event=customer.activation.start customerId=CUS-1002 status=PROSPECT correlation=lab-request-001`

## Debug / design challenge

Rewrite `log.info("created {}", customer)` — the `customer` argument invokes toString() and leaks email. Never pass whole domain objects to the logger. Log only safe, explicit fields: `log.info("event=customer.created customerId={} status={} correlation={}", customer.getId(), customer.getStatus(), correlationId);`

## Predict the output / behavior

No — fullName is not allowed even if the ticket already identifies Amina. The log store has a wider and longer-lived audience than one support ticket (aggregators, dashboards, archives, and people without ticket access), and correlation to a name belongs in the ticketing system, not the logs. Keep logs to safe identifiers like customerId so they stay PII-free regardless of who reads them.

## Scope

Pre-lab only.