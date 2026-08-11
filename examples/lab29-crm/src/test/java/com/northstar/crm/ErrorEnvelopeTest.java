package com.northstar.crm;

import com.northstar.crm.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ErrorEnvelopeTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  JwtService jwtService;

  private String agentBearer() {
    return "Bearer " + jwtService.issueToken("agent1", "AGENT");
  }

  @Test
  void validationReturns400Envelope() throws Exception {
    String badBody = "{\"id\":\"\",\"name\":\"\",\"email\":\"not-an-email\",\"status\":\"ACTIVE\"}";
    mvc.perform(post("/api/customers")
            .header("Authorization", agentBearer())
            .header("X-Correlation-Id", "lab-request-001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(badBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.correlationId").value("lab-request-001"))
        .andExpect(jsonPath("$.violations").isNotEmpty());
  }

  @Test
  void missingCustomerReturns404Envelope() throws Exception {
    mvc.perform(get("/api/customers/CUS-9999")
            .header("Authorization", agentBearer())
            .header("X-Correlation-Id", "lab-request-001"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.correlationId").value("lab-request-001"));
  }

  @Test
  void duplicateReturns409Envelope() throws Exception {
    String duplicate = "{\"id\":\"CUS-1001\",\"name\":\"Amina Khan\","
        + "\"email\":\"amina.khan@example.com\",\"status\":\"ACTIVE\"}";
    mvc.perform(post("/api/customers")
            .header("Authorization", agentBearer())
            .header("X-Correlation-Id", "lab-request-001")
            .contentType(MediaType.APPLICATION_JSON)
            .content(duplicate))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409));
  }

  @Test
  void securityStillRequiresToken() throws Exception {
    mvc.perform(get("/api/customers/CUS-1001"))
        .andExpect(status().isUnauthorized());
  }
}
