# Lab 20 — Fill Forbidden PII Checklist TODOs

Forbidden: email address
Forbidden: phone number
Forbidden: raw national ID / card PAN
Allowed customerId: CUS-1001 / CUS-1002
Allowed correlation: lab-request-001
Clear MDC in finally? yes

## Finally snippet

try { MDC.put("corr", "lab-request-001"); ... } finally { MDC.clear(); }

## Debug / design challenge

Yes, "Amina" alone is forbidden even without email. A first/full name is personal data by itself; log the customerId instead.

## Predict the output / behavior

No, ERROR logs may not include the request JSON body "just this once." The body can carry names, emails, and tokens, and ERROR lines are the most widely read and retained. Log safe fields plus a reason code.

## Scope

Pre-lab only.