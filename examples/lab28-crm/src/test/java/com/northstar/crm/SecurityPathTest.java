package com.northstar.crm;

import com.northstar.crm.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityPathTest {

  @Autowired
  MockMvc mvc;

  @Autowired
  JwtService jwtService;

  @Test
  void missingTokenIs401() throws Exception {
    mvc.perform(get("/api/customers/CUS-1001"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void agentCanReadCustomerButNotAdmin() throws Exception {
    String token = jwtService.issueToken("agent1", "AGENT");
    mvc.perform(get("/api/customers/CUS-1001").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
    mvc.perform(get("/api/admin/ping").header("Authorization", "Bearer " + token))
        .andExpect(status().isForbidden());
  }

  @Test
  void adminCanPing() throws Exception {
    String token = jwtService.issueToken("admin1", "ADMIN");
    mvc.perform(get("/api/admin/ping").header("Authorization", "Bearer " + token))
        .andExpect(status().isOk());
  }
}
