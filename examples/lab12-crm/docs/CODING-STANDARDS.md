# Coding Standards Self-Check — Lab 12

| # | Confirm | Result |
| - | ------- | ------ |
| 1 | Meaningful type and method names | Pass — createCustomer/getCustomer/updateStatus, customersById, helpers |
| 2 | No raw types in new code | Pass — Map<String, Customer>, no raw List |
| 3 | Validation in clear helpers | Pass — requireNonBlank / requireUniqueId / requireExisting |
| 4 | Exceptions instead of null for errors | Pass — IllegalArgumentException / IllegalStateException |
| 5 | No production secrets / no PII beyond lab sample emails | Pass — only example.com sample data |
| 6 | Service still compiles without Spring/JPA/Kafka | Pass — plain Java 21, no framework imports |