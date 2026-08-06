# Lab 24 — PayloadRoot Skeleton

## Class annotation

`@Endpoint` on `CustomerEndpoint` (marks it as a Spring WS message-handling component, discovered by the MessageDispatcher).

## @PayloadRoot localPart

`localPart = "GetCustomerRequest"` with `namespace = NAMESPACE`, where `NAMESPACE` equals the `targetNamespace` in `customer.xsd`. Both must match the XSD exactly.

## Method inputs/outputs

`getCustomer(@RequestPayload GetCustomerRequest req)` returns `@ResponsePayload GetCustomerResponse` (both JAXB types generated from the XSD).

## Delegation line (words)

Map the request id out of `GetCustomerRequest`, call `customerService.get(id)`, then map the returned `Customer` into a `GetCustomerResponse` and return it. The endpoint only translates XML to/from the domain; the service does the work.

```java
@Endpoint
public class CustomerEndpoint {
  private static final String NAMESPACE = "http://northstar.com/crm/customers";
  private final CustomerService customerService;
  public CustomerEndpoint(CustomerService customerService) { this.customerService = customerService; }

  @PayloadRoot(namespace = NAMESPACE, localPart = "GetCustomerRequest")
  @ResponsePayload
  public GetCustomerResponse getCustomer(@RequestPayload GetCustomerRequest req) {
    // map req -> id, delegate, map Customer -> response
  }
}
```

## Debug / design challenge

If `localPart` is `GetCustomer` but the XSD says `GetCustomerRequest`, the MessageDispatcher never finds a matching handler for the incoming payload's root element. The request is unmapped, so the client gets a fault (no endpoint) rather than a response.

## Predict the output / behavior

No — `@PayloadRoot` does not replace `MessageDispatcherServlet` configuration. You still register the servlet (mapped to `/ws/*`) and the WSDL/XSD beans; `@PayloadRoot` only routes a payload to a method once the dispatcher servlet is receiving SOAP requests.

## Scope

Pre-lab only.
