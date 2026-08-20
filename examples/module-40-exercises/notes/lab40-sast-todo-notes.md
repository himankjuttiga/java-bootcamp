# Lab 40 — Fill SAST Path TODOs

## Step 1 — Copy template

```
Endpoint: _____
Authz check: _____
Sink (SQL/file/log): _____
Customer fixture used: _____
Risk if missing check: _____
```

## Step 2 — Fill for customer read

```
Endpoint: GET /api/customers/{publicId}
  CustomerController#get -> CustomerService#getByPublicId -> CustomerRepository#findByPublicId
Authz check: NONE TODAY. Two are required and neither exists:
  role check (agent role) TODO — no spring-boot-starter-security on the classpath
  object-level check (is this agent allowed to read this customer) TODO — Lab 40 code proof
Sink (SQL/file/log): SQL via derived query, parameters bound by Spring Data, no concatenation.
  Log sink on the failure path only: ApiExceptionHandler#notFound logs correlationId.
Customer fixture used: CUS-1001 Amina Khan, ACTIVE, amina@example.test
Risk if missing check: any caller enumerates CUS-1001 through CUS-9999 and harvests name and
  email for every customer. Broken access control, object level, and it reads as normal traffic.
```

## Step 3 — Second path

```
Endpoint: PATCH /api/customers/{publicId}/status?status=ACTIVE
  CustomerController#changeStatus -> CustomerService#changeStatus -> CustomerRepository#findByPublicId
Authz check: NONE TODAY.
  role check (only an agent may change status) TODO
  object-level check (this agent owns this customer) TODO — Lab 40 code proof
  transition rule (PROSPECT -> ACTIVE -> CLOSED) TODO, currently any listed value is accepted
Sink (SQL/file/log): UPDATE customer ... WHERE customer_id = ? AND version = ?, parameters bound.
  Optimistic-lock failure logs through ApiExceptionHandler#optimisticLock with correlationId.
Customer fixture used: CUS-1002 Ravi Singh, PROSPECT -> ACTIVE, ravi@example.test
Risk if missing check: an unauthenticated caller closes or reactivates accounts. Ravi becomes
  ACTIVE without a sale, or Amina becomes CLOSED and an agent loses access to a live account.
```

## Step 4 — Self-check

No passwords, tokens or real PII appear above; both fixtures are synthetic and use `@example.test`.

Blanks that stay blank until Lab 40 proves them in code:

| Item | Why it is still open |
| ---- | -------------------- |
| Object-level authz on both paths | needs a negative test showing agent B is refused CUS-1001 |
| Role check on all four routes | needs Spring Security on the classpath first |
| Status transition rule | needs a service rule plus a test, not a check constraint |
| Whether Hibernate's logged message carries the bound email | needs one duplicate POST and a look at the log line |

## Scope

Pre-lab only — do not finish the full graded lab in this exercise.
