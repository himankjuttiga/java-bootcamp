# Lab 24 — SOAP Notes

## Contract-first

`src/main/resources/customer.xsd` is the source of truth (namespace `http://northstar.com/crm/customers`). Spring WS generates the WSDL from it dynamically. Timed path = `getCustomer` only.

## Endpoints and URLs

- WSDL: `http://localhost:8080/ws/customers.wsdl` (port type `CustomersPort`)
- SOAP endpoint: POST `http://localhost:8080/ws` with `requests/get-customer.xml`
- REST (unchanged): `GET http://localhost:8080/api/customers/CUS-1001`

## One service, two protocols

`CustomerEndpoint` (SOAP) and `CustomerController` (REST) both depend on the single `CustomerService` bean. `CustomerSoapMapper` keeps DOM/XML mapping out of the service and REST layers. A SOAP `getCustomer` for CUS-1001 and a REST GET for CUS-1001 return the same customer data — proof the business rules are shared, not forked.

## Scope

- Timed path: getCustomer only, DOM `Element` mapper, unsecured, `CustomersPort`.
- Full path (homework): four operations, JAXB/XJC, `SoapFaultMappingExceptionResolver`, and a lab-only Wss4j UsernameToken interceptor. UsernameToken is **not wired** in this timed build.
- Missing id (CUS-9999) currently surfaces as a server-side fault from `IllegalArgumentException`; mapping it to a CLIENT fault is full-path work.
