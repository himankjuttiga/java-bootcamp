# Lab 24 — Contract-First Recall

## Order (fill)

1. Author the XSD (customer.xsd) defining elements and types — the contract.
2. Generate JAXB classes from the XSD (request/response types).
3. Implement the `@Endpoint` that maps the generated types and delegates to `CustomerService`.
4. Serve the WSDL (generated from the XSD) so partners can bind to it.

## Source of truth

`customer.xsd` — the schema, not hand-written DTO fields. The WSDL, JAXB types, and endpoint all derive from it.

## Why partners care

Partner billing and integration tools bind to the XML element names (e.g. `GetCustomerRequest`, `GetCustomerResponse`). Those names must stay stable, because a change to them breaks every consumer that is wired to the contract.

## Debug / design challenge

If someone adds a Java field without updating the XSD, nothing changes for SOAP clients — the field is not in the schema, so JAXB never marshals it into the XML and it silently never appears on the wire. Worse, editing only Java can drift the implementation away from the published contract, so the code and the WSDL disagree.

## Predict the output / behavior

No — code-first WSDL export is not the Lab 24 primary approach. Lab 24 is contract-first: the XSD is authored first and the WSDL/JAXB/endpoint follow from it, never the reverse.

## Scope

Pre-lab only.
