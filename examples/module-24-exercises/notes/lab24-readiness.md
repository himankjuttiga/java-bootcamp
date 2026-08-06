# Lab 24 readiness checklist

| File | Present? (yes/no) |
| ---- | ----------------- |
| notes/contract-first.md | yes |
| notes/soap-ops.md | yes |
| notes/lab24-payloadroot-skeleton.md | yes |
| notes/fault-vs-rest.md | yes |
| notes/usernametoken-plan.md | yes |

## Scope

Pre-lab only. Keep REST? Yes — SOAP is added beside the existing REST API, sharing one CustomerService.

## Self mark

Overall prep: Pass
If Fail, revisit: the exercise for whichever note is missing or still has a blank

## Debug / design challenge

If the contract-first notes say Java is the source of truth, reopen Exercise 1 (Contract-First Recall) — the XSD is the source of truth.

## Predict the output / behavior

After `spring-boot:run`, the WSDL is served at `http://localhost:8080/ws/customers.wsdl` (the MessageDispatcherServlet mapped under `/ws/*`).
