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
 * Real PostgreSQL, never H2 in compatibility mode: identity generation, TIMESTAMPTZ and
 * constraint-violation behaviour all differ, so a green H2 run would prove nothing about the
 * engine this application actually talks to.
 *
 * The starter offers two ways to get that engine: "Prefer Testcontainers PostgreSQL when
 * available; otherwise point at local compose." Testcontainers is unavailable on this machine,
 * because Docker Engine 29 removed the REST API versions its Docker client speaks (/v1.32/info
 * answers 400 while /v1.44/info answers 200), so this suite uses the compose container from
 * compose.yaml on port 5432.
 *
 * The tradeoff, stated rather than hidden: the database is shared rather than fresh per run, so
 * each test clears the tables it touches instead of relying on a new container. Fidelity to the
 * engine, which is the property that matters, is unchanged.
 *
 * Flyway applies V1 and ddl-auto=validate means the context only starts if every entity agrees
 * with that schema. Context startup is itself an assertion.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerRepositoryIT {

  @DynamicPropertySource
  static void datasource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> env("SPRING_DATASOURCE_URL",
        "jdbc:postgresql://localhost:5432/crm"));
    registry.add("spring.datasource.username", () -> env("SPRING_DATASOURCE_USERNAME", "crm"));
    // No fallback for the password: a credential that works belongs in .env, never in a
    // committed test. Missing means a clear failure, not a silent default.
    registry.add("spring.datasource.password", () -> require("SPRING_DATASOURCE_PASSWORD"));
  }

  private static String env(String name, String fallback) {
    String value = System.getenv(name);
    return (value == null || value.isBlank()) ? fallback : value;
  }

  private static String require(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException(
          name + " is not set. Run: cd examples/lab39-crm && set -a; source .env; set +a");
    }
    return value;
  }

  @Autowired CustomerRepository repository;
  @Autowired AccountRepository accountRepository;
  @Autowired TestRestTemplate rest;

  @BeforeEach
  void reset() {
    accountRepository.deleteAll();
    repository.deleteAll();
  }

  @Test
  void saveAndFindByPublicId() {
    repository.save(new CustomerEntity("CUS-1001", "Amina Khan", "amina@example.com", "ACTIVE"));

    CustomerEntity amina = repository.findByPublicId("CUS-1001").orElseThrow();

    assertThat(amina.getCustomerId()).isNotNull();      // BIGSERIAL assigned by the database
    assertThat(amina.getFullName()).isEqualTo("Amina Khan");
    assertThat(amina.getStatus()).isEqualTo("ACTIVE");
    assertThat(amina.getVersion()).isZero();            // @Version starts at 0
    assertThat(amina.getCreatedAt()).isNotNull();       // TIMESTAMPTZ round-trips as Instant
    assertThat(repository.findByPublicId("CUS-9999")).isEmpty();
  }

  @Test
  void duplicateEmailViolatesTheUniqueConstraint() {
    repository.saveAndFlush(
        new CustomerEntity("CUS-1001", "Amina Khan", "amina@example.com", "ACTIVE"));

    assertThatThrownBy(
            () ->
                repository.saveAndFlush(
                    new CustomerEntity("CUS-DUPE", "Impostor", "amina@example.com", "PROSPECT")))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void invalidStatusIsRejectedByTheCheckConstraint() {
    assertThatThrownBy(
            () ->
                repository.saveAndFlush(
                    new CustomerEntity("CUS-BAD", "Bad Status", "bad@example.com", "UNKNOWN")))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  void pagingIsDeterministicWithTheIdTieBreaker() {
    for (int i = 1; i <= 25; i++) {
      repository.save(
          new CustomerEntity(
              "CUS-2%03d".formatted(i), "Bulk " + i, "bulk%03d@example.com".formatted(i), "ACTIVE"));
    }
    repository.save(new CustomerEntity("CUS-1002", "Ravi Singh", "ravi@example.com", "PROSPECT"));

    Sort sort =
        Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "customerId"));
    Page<CustomerEntity> first = repository.findByStatus("ACTIVE", PageRequest.of(0, 20, sort));
    Page<CustomerEntity> second = repository.findByStatus("ACTIVE", PageRequest.of(1, 20, sort));

    assertThat(first.getTotalElements()).isEqualTo(25);   // Ravi is PROSPECT, correctly excluded
    assertThat(first.getContent()).hasSize(20);
    assertThat(second.getContent()).hasSize(5);
    assertThat(first.getContent()).doesNotContainAnyElementsOf(second.getContent());
  }

  @Test
  void concurrentUpdatesFailTheSecondWriterInsteadOfLosingIt() {
    CustomerEntity saved =
        repository.saveAndFlush(
            new CustomerEntity("CUS-1002", "Ravi Singh", "ravi@example.com", "PROSPECT"));
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
            new CustomerEntity("CUS-1001", "Amina Khan", "amina@example.com", "ACTIVE"));
    CustomerEntity ravi =
        repository.saveAndFlush(
            new CustomerEntity("CUS-1002", "Ravi Singh", "ravi@example.com", "PROSPECT"));
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
        new CreateCustomerRequest("CUS-1001", "Amina Khan", "amina@example.com", "ACTIVE");
    assertThat(rest.postForEntity("/api/customers", request, String.class).getStatusCode())
        .isEqualTo(HttpStatus.CREATED);

    ResponseEntity<String> conflict =
        rest.postForEntity(
            "/api/customers",
            new CreateCustomerRequest("CUS-OTHER", "Impostor", "amina@example.com", "PROSPECT"),
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
        rest.getForEntity("/api/customers?status=ACTIVE&sort=password", String.class);

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
