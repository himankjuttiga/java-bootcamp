# AI Test and Refactor Notes — Lab 11 (Northstar CRM)

## lab11-001 — Rejected false-confidence assertion

**Weak test Copilot tends to generate (or was inherited as PlaceholderTest):**

```java
@Test
void serviceIsNotNull() {
    assertNotNull(service);
}
```

**Why it is false confidence:**
This assertion can never fail. The @BeforeEach method always assigns a fresh
CustomerService before every test, so `service` is guaranteed non-null. The test
executes code but verifies no business behaviour — it passes whether the CRM
logic is correct or completely broken. It inflates the test count and the
coverage number while protecting nothing.

**Action taken:**
Rejected and deleted. (An equivalent trivial test, PlaceholderTest with
`assertTrue(true)`, was also removed from the suite for the same reason.)

**Replaced with a real assertion** — a meaningful test of findByStatus:

```java
@Test
void findByStatusReturnsOnlyMatchingCustomers() {
    Customer amina = new Customer("CUS-1001", "Amina Khan", "amina.khan@example.com",
            "555-0101", CustomerStatus.ACTIVE, LocalDateTime.now());
    Customer ravi = new Customer("CUS-1002", "Ravi Singh", "ravi.singh@example.com",
            "555-0102", CustomerStatus.PROSPECT, LocalDateTime.now());
    service.addCustomer(amina);
    service.addCustomer(ravi);

    assertEquals(1, service.findByStatus(CustomerStatus.PROSPECT).size());
    assertEquals("CUS-1002",
            service.findByStatus(CustomerStatus.PROSPECT).get(0).getCustomerId());
}
```

This test can fail: if findByStatus stopped filtering correctly, the size or the
id assertion would break immediately.

## lab11-002 — Code smell: duplicated / inconsistent validation

**Smell identified:**
Duplicated validation logic. The blank-customerId check lived inline inside
addCustomer, while updateStatus performed no id validation at all. This is both
duplication (the rule is a concept that belongs in one place) and an
inconsistency (two entry points treated the same invalid input differently).

**Refactor applied:**
Extracted a single private helper, validateCustomerId(String), that throws
IllegalArgumentException on a null or blank id. Both addCustomer and updateStatus
now call it. The blank-id rule now exists in exactly one place.

**Tests proving behaviour is unchanged:**
Full suite run before and after the refactor — 8 tests, 0 failures both times
(CustomerTest 2, CustomerServiceTest 5, CustomerNotifierMockTest 1). No test
required modification, confirming observable behaviour was preserved while the
internal structure improved.

## lab11-003 — Coverage gap review

### Customer (entity)
| Method | Covered? | By which test |
| ------ | -------- | ------------- |
| equals / hashCode | Yes | CustomerTest.equalsIsBasedOnCustomerIdOnly |
| toString | Yes | CustomerTest.toStringIncludesCustomerId |
| getters / setters | Partial | Exercised indirectly by service tests, not asserted directly |
| no-arg constructor | No | Not directly tested |

### CustomerService
| Method | Covered? | By which test |
| ------ | -------- | ------------- |
| addCustomer (happy path) | Yes | addCustomerStoresNewCustomer |
| addCustomer (duplicate) | Yes | addCustomerRejectsDuplicateId |
| addCustomer (null / blank id) | No | validateCustomerId path not directly tested |
| updateStatus (happy path) | Yes | updateStatusChangesExistingCustomer |
| updateStatus (unknown id) | Yes | updateStatusThrowsForUnknownCustomer |
| updateStatus (notifier call) | Yes | CustomerNotifierMockTest |
| findByCustomerId | Partial | Used by other tests, no dedicated test |
| findByStatus | Yes | findByStatusReturnsOnlyMatchingCustomers |
| listAll | Partial | Asserted via size/get in addCustomer test |
| create / getById (Lab 8 stubs) | No | Throw UnsupportedOperationException by design; not in scope |

### Honest gap assessment
- The blank/null id validation (validateCustomerId) is not directly tested,
  even though it now guards two methods. Acceptable now; a dedicated test would
  strengthen it and belongs in the Labs 17–18 expansion.
- findByCustomerId and listAll are only indirectly covered. Acceptable for this
  preview; low risk given their simplicity.
- The Lab 8 create/getById stubs are intentionally unimplemented and out of
  scope for Lab 11.
- Coverage here reflects which code executed, not proof every behaviour is
  correct. This is a guided preview; formal methodology arrives in Labs 17–18.
- 
## lab11-004 — Acceptance guidelines for AI-generated tests and refactors

1. Every assertion must be able to fail. If I cannot describe an input that
   breaks it, it is not a real test (see lab11-001).
2. Every refactor must be backed by a full test-suite run before and after,
   both green, proving observable behaviour is unchanged (see lab11-002).
3. No accepted suggestion may introduce a dependency not already in pom.xml,
   nor a Spring/JPA construct — this is plain Java for Week 2.
4. Tests must use the shared Northstar fixtures (CUS-1001 Amina, CUS-1002 Ravi),
   never invented data, so fixtures stay consistent across labs.
5. I can explain, without re-reading Copilot's explanation, why each accepted
   test and refactor is correct. Coverage gaps are documented, not hidden
   (see lab11-003).