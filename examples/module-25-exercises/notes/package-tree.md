# Lab 25 — Package Sketch

## Tree

```
com.northstar.crm
  CrmApplication            (scan root, @SpringBootApplication)
  api/
    CustomerController      (@RestController)
  service/
    CustomerService         (@Service — business rules)
  repository/
    CustomerRepository      (interface)
    InMemoryCustomerRepository (@Repository impl)
  model/
    Customer                (plain domain type)
```

## Where does the controller live?

`com.northstar.crm.api` — the web layer, holding `CustomerController`.

## Where does InMemoryCustomerRepository live?

`com.northstar.crm.repository`, alongside the `CustomerRepository` interface it implements.

## Debug / design challenge

No — SOAP endpoints should not sit under `repository`. They are an inbound protocol adapter (like controllers), so they belong in their own `endpoint` (or `api`) package and delegate to the service. `repository` is strictly outbound persistence.

## Predict the output / behavior

Keep `model` free of Spring Web annotations so the domain type stays a plain, framework-agnostic object usable by REST, SOAP, tests, and any future persistence layer without dragging web concerns into the core.

## Scope

Pre-lab only.
