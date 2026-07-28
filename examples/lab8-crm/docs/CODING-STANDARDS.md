# Northstar CRM Coding Standards (Lab 8)

## Layers
- controller: transport / API mapping only
- service: business rules
- repository: persistence
- entity: domain model
- dto: request/response contracts
- config: wiring
- exception: domain and API failures

## Hard rules
- Services must not depend on controllers.
- Entities must not carry HTTP or SOAP types.
- Repositories must not import controllers (or ideally DTOs).
- Dependencies point one way: controller -> service -> repository -> entity.
- No production passwords or API keys in source or properties.
- Stubs may throw UnsupportedOperationException. That is success for Lab 8, not a bug.

## Naming
- Classes PascalCase: CustomerService, CustomerRepository.
- Methods intention-revealing: findById, save, create, getById.
- Customer IDs use the CUS-#### format (CUS-1001).
- Packages lowercase, reverse-domain: com.northstar.crm.service.

## What not to commit
- target/, .idea/, *.iml
- secrets, .env, real customer PII

## Tooling
- JDK 21, Maven. Format on save encouraged.