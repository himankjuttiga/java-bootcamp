package com.northstar.crm.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.northstar.crm.customer.CustomerEntity;
import com.northstar.crm.customer.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Regression test for lab40-001, broken object-level access control.
 *
 * Written red against the Lab 39 baseline, where no route authenticated and no read was scoped
 * to an owner: every assertion below returned 200. It passes only with V2's owner_agent column,
 * SecurityConfig and CustomerService#requireOwner in place, which is what makes it evidence
 * rather than decoration.
 *
 * Policy asserted here: 403 for a customer that exists but belongs to another agent, 404 for one
 * that does not exist, 401 for no credentials. The disclosure tradeoff is recorded in
 * docs/security-assessment.md.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ObjectOwnershipSecurityTest {

  private static final String AGENT_A = "agent-a";
  private static final String AGENT_B = "agent-b";
  private static final String CORRELATION = "lab-request-001";

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> env("SPRING_DATASOURCE_URL",
        "jdbc:postgresql://localhost:5432/crm"));
    registry.add("spring.datasource.username", () -> env("SPRING_DATASOURCE_USERNAME", "crm"));
    registry.add("spring.datasource.password", () -> require("SPRING_DATASOURCE_PASSWORD"));
    registry.add("crm.agents.agent-a", () -> "test-only-agent-a");
    registry.add("crm.agents.agent-b", () -> "test-only-agent-b");
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

  @Autowired MockMvc mvc;
  @Autowired CustomerRepository repository;

  /** Amina belongs to agent-a, Ravi to agent-b. Synthetic fixtures, @example.test addresses. */
  @BeforeEach
  void seed() {
    repository.deleteAll();
    repository.saveAndFlush(
        new CustomerEntity("CUS-1001", "Amina Khan", "amina.khan@example.test", "ACTIVE", AGENT_A));
    repository.saveAndFlush(
        new CustomerEntity("CUS-1002", "Ravi Singh", "ravi.singh@example.test", "PROSPECT", AGENT_B));
  }

  @Test
  @WithMockUser(username = AGENT_A, roles = "AGENT")
  void agentCannotReadAnotherAgentsCustomer() throws Exception {
    mvc.perform(get("/api/customers/{publicId}", "CUS-1002").header("X-Correlation-Id", CORRELATION))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.correlationId").value(CORRELATION));
  }

  @Test
  @WithMockUser(username = AGENT_A, roles = "AGENT")
  void agentCanReadTheirOwnCustomer() throws Exception {
    mvc.perform(get("/api/customers/{publicId}", "CUS-1001").header("X-Correlation-Id", CORRELATION))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.publicId").value("CUS-1001"));
  }

  @Test
  @WithMockUser(username = AGENT_A, roles = "AGENT")
  void agentCannotChangeAnotherAgentsCustomerStatus() throws Exception {
    mvc.perform(
            patch("/api/customers/{publicId}/status", "CUS-1002")
                .param("status", "CLOSED")
                .header("X-Correlation-Id", CORRELATION))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(username = AGENT_A, roles = "AGENT")
  void listReturnsOnlyTheCallersCustomers() throws Exception {
    mvc.perform(get("/api/customers").param("status", "ACTIVE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.content[0].publicId").value("CUS-1001"));
  }

  @Test
  @WithMockUser(username = AGENT_A, roles = "AGENT")
  void unknownCustomerIsNotFoundRatherThanForbidden() throws Exception {
    mvc.perform(get("/api/customers/{publicId}", "CUS-9999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void anonymousRequestIsUnauthorized() throws Exception {
    mvc.perform(get("/api/customers/{publicId}", "CUS-1001"))
        .andExpect(status().isUnauthorized());
  }
}
