# Lab 24 — UsernameToken Plan

## Where credentials live

In the SOAP Header, as a WS-Security `wsse:UsernameToken` (a lab username + lab password, PasswordText teaching mode). It travels inside the SOAP envelope, not as an HTTP header.

## Success case

A secured `GetCustomer` for CUS-1001 that carries a valid UsernameToken passes the security interceptor and reaches `CustomerService`, returning the customer.

## Failure case

A missing or wrong token is rejected by the security interceptor and returns a WS-Security fault **before** the service method is ever called.

## Out of scope

Full XML signatures, encryption, SAML, and OAuth/JWT identity providers. REST JWT auth is Lab 28.

## Debug / design challenge

No — PasswordText UsernameToken is not enough without HTTPS in production. The password travels in clear text inside the envelope, so without TLS anyone on the wire can read it. Message-level auth proves the partner presented credentials, but transport-level TLS is still required.

## Predict the output / behavior

No — UsernameToken does not replace constructor DI on `CustomerService`. It is a message-level authentication concern handled by a security interceptor; the service is still a Spring bean wired by constructor injection, unchanged.

## Scope

Pre-lab only.
