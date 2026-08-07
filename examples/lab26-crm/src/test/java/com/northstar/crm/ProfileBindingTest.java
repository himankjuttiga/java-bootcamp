package com.northstar.crm;

import com.northstar.crm.config.NorthstarIntegrationProperties;
import com.northstar.crm.model.Customer;
import com.northstar.crm.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ProfileBindingTest {

  @Autowired
  NorthstarIntegrationProperties properties;

  @Autowired
  CustomerService customerService;

  @Test
  void bindsTestProfileConfigurationAndFixtures() {
    // Profile YAML value wins for the test profile
    assertThat(properties.getConnectTimeoutMs()).isEqualTo(100);
    // api-base-url resolves from base / test YAML
    assertThat(properties.getApiBaseUrl()).isEqualTo("http://localhost:9090");
    // CRM fixtures remain intact
    Customer amina = customerService.get("CUS-1001");
    assertThat(amina.getName()).isEqualTo("Amina Khan");
    assertThat(amina.getStatus()).isEqualTo("ACTIVE");
  }
}
