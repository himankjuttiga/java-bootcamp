# Lab 24 — SOAP Fault Versus REST Error

| Case | SOAP | REST |
| --- | --- | --- |
| Missing customer (CUS-9999) | SOAP Fault (Client/business fault) | HTTP 404 JSON problem details |
| Validation fail | SOAP Fault (Client fault) | HTTP 400 JSON |
| Missing UsernameToken | WS-Security fault (before service call) | HTTP 401/403 (JWT, later Lab 28) |

## One rule

One `CustomerService` exception (e.g. `NotFoundException`) drives both channels through different protocol adapters — a SOAP fault on `/ws`, an HTTP status + JSON on REST. Never return a REST JSON body on the SOAP channel.

## Debug / design challenge

No — `CustomerEndpoint` should not catch `Exception` and always return a generic SERVER fault. That hides business meaning (a not-found should be a Client-side business fault, not a server error) and swallows real server bugs. Map specific exceptions to appropriate fault codes; let unexpected ones surface as genuine server faults.

## Predict the output / behavior

`NotFoundException` should be translated to a SOAP fault at the SOAP boundary — via a Spring WS exception resolver (e.g. `SoapFaultMappingExceptionResolver` or a `@SoapFault`-annotated exception), not inside `CustomerService`. The service throws once; each adapter translates it for its protocol.

## Scope

Pre-lab only.
