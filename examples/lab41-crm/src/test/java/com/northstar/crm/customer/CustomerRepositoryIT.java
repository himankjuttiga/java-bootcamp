package com.northstar.crm.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.northstar.crm.account.AccountEntity;
import com.northstar.crm.account.AccountRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

/**
 * Carried forward from Lab 39, updated for the Lab 40 remediation: every customer now has an
 * owning agent, and every HTTP call authenticates.
 *
 * Real PostgreSQL, never H2 in compatibility mode: identity generation, TIMESTAMPTZ and
 * constraint-violation behaviour all differ. The database comes from compose.yaml on port 5432.
 * Flyway applies V1 and V2, and ddl-auto=validate means the context only starts if every entity
 * agrees with that schema, so context startup is itself an assertion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerRepositoryIT {

  static final String AGENT_A = "agent-a";
  static final String AGENT_B = "agent-b";

  // Test-only values. These configure the in-memory agents for this suite and are credentials
  // to nothing outside it; the running application reads its agents from the environment.
  static final String AGENT_A_PASSWORD = "test-only-agent-a";
  static final String AGENT_B_PASSWORD = "test-only-agent-b";

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> env("SPRING_DATASOURCE_URL",
        "jdbc:postgresql://localhost:5432/crm"));
    registry.add("spring.datasource.username", () -> env("SPRING_DATASOURCE_USERNAME", "crm"));
    // No fallback for the password: a credential that works belongs in .env, never in a
    // committed test. Missing means a clear failure, not a silent default.
    registry.add("spring.datasource.password", () -> require("SPRING_DATASOURCE_PASSWORD"));
    registry.add("crm.agents.agent-a", () -> AGENT_A_PASSWORD);
    registry.add("crm.agents.agent-b", () -> AGENT_B_PASSWORD);
  }

  private static String env(String name, String fallback) {
    String value = System.getenv(name);
    return (value == null || value.isBlank()) ? fallback : value;
  }

  private static String require(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException(
          name + " is not set. Run: cd examples/lab40-crm && set -a; source .env; set +a");
    }
    return value;
  }

  @Autowired CustomerRepository repository;
  @Autowired AccountRepository accountRepository;
  @Autowired TestRestTemplate rest;

  private TestRestTemplate asAgentA() {
    return rest.withBasicAuth(AGENT_A, AGENT_A_PASSWORD);
  }

  @BeforeEach
  void reset() {
    accountRepository.deleteAll();
    repository.deleteAll();
  }

  @Test
  void saveAndFindByPublicId() {
    repository.save(
        new CustomerEntity("CUS-1001", "Amina Khan", "amina.khan@example.test", "ACTIVE", AGENT_A));

    CustomerEntity amina = repository.findByPublicId("CUS-1001").orElseThrow();

    assertThat(amina.getCustomerId()).isNotNull();      // BIGSERIAL assigned by the database
    assertThat(amina.getFullName()).isEqualTo("Amina Khan");
    assertThat(amina.getStatus()).isEqualTo("ACTIVE");
    assertThat(amina.getOwnerAgent()).isEqualTo(AGENT_A);
    assertThat(amina.getVersion()).isZero();            // @Version starts at 0
    assertThat(amina.getCreatedAt()).isNotNull();       // TIMESTAMPTZ round-trips as Instant
    assertThat(repository.findByPublicId("CUS-9999")).isEmpty();
  }

  @Test
  void duplicateEmailViolatesTheUniqueConstraint() {
    repository.saveAndFlush(
        new CustomerEntity("CUS-1001", "Amina Khan", "amina.khan@example.test", "ACTIVE", AGENT_A));

    assertThatThrownBy(
            () ->
                repository.saveAndFlush(
                    new CustomerEntity(
                        "CUS-DUPE", "Impostor", "amina.khan@example.test", "PROSPECT", AGENT_B)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void invalidStatusIsRejectedByTheCheckConstraint() {
    assertThatThrownBy(
            () ->
                repository.saveAndFlush(
                    new CustomerEntity(
                        "CUS-BAD", "Bad Status", "bad@example.test", "UNKNOWN", AGENT_A)))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void pagingIsDeterministicWithTheIdTieBreaker() {
    for (int i = 1; i <= 25; i++) {
      repository.save(
          new CustomerEntity(
              "CUS-2%03d".formatted(i),
              "Bulk " + i,
              "bulk%03d@example.test".formatted(i),
              "ACTIVE",
              AGENT_A));
    }
    repository.save(
        new CustomerEntity("CUS-1002", "Ravi Singh", "ravi.singh@example.test", "PROSPECT", AGENT_A));

    Sort sort =
        Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "customerId"));
    Page<CustomerEntity> first =
        repository.findByOwnerAgentAndStatus(AGENT_A, "ACTIVE", PageRequest.of(0, 20, sort));
    Page<CustomerEntity> second =
        repository.findByOwnerAgentAndStatus(AGENT_A, "ACTIVE", PageRequest.of(1, 20, sort));

    assertThat(first.getTotalElements()).isEqualTo(25);   // Ravi is PROSPECT, correctly excluded
    assertThat(first.getContent()).hasSize(20);
    assertThat(second.getContent()).hasSize(5);
    assertThat(first.getContent()).doesNotContainAnyElementsOf(second.getContent());
  }

  @Test
  void concurrentUpdatesFailTheSecondWriterInsteadOfLosingIt() {
    CustomerEntity saved =
        repository.saveAndFlush(
            new CustomerEntity(
                "CUS-1002", "Ravi Singh", "ravi.singh@example.test", "PROSPECT", AGENT_A));
    Long id = saved.getCustomerId();

    CustomerEntity writerA = repository.findById(id).orElseThrow();
    CustomerEntity writerB = detachedCopyAtSameVersion(id);

    writerA.setStatus("ACTIVE");
    repository.saveAndFlush(writerA);          // wins, version 0 -> 1

    writerB.setStatus("CLOSED");
    assertThatThrownBy(() -> repository.saveAndFlush(writerB))
        .isInstanceOf(OptimisticLockingFailureException.class);

    assertThat(repository.findById(id).orElseThrow().getStatus()).isEqualTo("ACTIVE");
  }

  @Test
  void ravisAccountListIsEmptyRatherThanAnError() {
    CustomerEntity amina =
        repository.saveAndFlush(
            new CustomerEntity(
                "CUS-1001", "Amina Khan", "amina.khan@example.test", "ACTIVE", AGENT_A));
    CustomerEntity ravi =
        repository.saveAndFlush(
            new CustomerEntity(
                "CUS-1002", "Ravi Singh", "ravi.singh@example.test", "PROSPECT", AGENT_A));
    accountRepository.saveAndFlush(
        new AccountEntity(amina.getCustomerId(), "ACCT-1001-01", 250_000L));

    List<AccountEntity> aminasAccounts = accountRepository.findByCustomerId(amina.getCustomerId());
    List<AccountEntity> ravisAccounts = accountRepository.findByCustomerId(ravi.getCustomerId());

    assertThat(aminasAccounts).hasSize(1);
    assertThat(aminasAccounts.get(0).getBalanceCents()).isEqualTo(250_000L);
    assertThat(ravisAccounts).isEmpty();
  }

  @Test
  void duplicateCreateReturns409WithoutLeakingSql() {
    CreateCustomerRequest request =
        new CreateCustomerRequest("CUS-1001", "Amina Khan", "amina.khan@example.test", "ACTIVE");
    assertThat(asAgentA().postForEntity("/api/customers", request, String.class).getStatusCode())
        .isEqualTo(HttpStatus.CREATED);

    ResponseEntity<String> conflict =
        asAgentA()
            .postForEntity(
                "/api/customers",
                new CreateCustomerRequest(
                    "CUS-OTHER", "Impostor", "amina.khan@example.test", "PROSPECT"),
                String.class);

    assertThat(conflict.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(conflict.getBody()).contains("correlationId").contains("lab-request-001");
    assertThat(conflict.getBody())
        .doesNotContain("insert into")
        .doesNotContain("uk_customer_email")
        .doesNotContain("org.hibernate");
  }

  @Test
  void unknownSortFieldIsRejectedWith400() {
    ResponseEntity<String> response =
        asAgentA().getForEntity("/api/customers?status=ACTIVE&sort=password", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  /**
   * Loads a second, independent instance of the same row. Each repository call outside a
   * transaction gets its own EntityManager, so both writers hold detached copies at version 0,
   * which is exactly the two-agents-editing-Amina situation.
   */
  private CustomerEntity detachedCopyAtSameVersion(Long id) {
    return repository.findById(id).orElseThrow();
  }
}
