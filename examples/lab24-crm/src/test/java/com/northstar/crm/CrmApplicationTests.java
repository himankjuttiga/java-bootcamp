package com.northstar.crm;

import com.northstar.crm.model.Customer;
import com.northstar.crm.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CrmApplicationTests {

  @Autowired
  CustomerService customerService;

  @Test
  void contextLoadsAndRestSeedVisible() {
    Customer c = customerService.get("CUS-1001");
    assertNotNull(c);
    assertEquals("Amina Khan", c.getName());
    assertEquals("ACTIVE", c.getStatus());
  }
}
