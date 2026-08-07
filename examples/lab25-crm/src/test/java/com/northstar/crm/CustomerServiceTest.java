package com.northstar.crm;

import com.northstar.crm.model.Customer;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import com.northstar.crm.service.CustomerService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTest {

  @Test
  void getSeededCus1001() {
    CustomerService service = new CustomerService(new InMemoryCustomerRepository());
    Customer amina = service.get("CUS-1001");
    assertEquals("CUS-1001", amina.getId());
    assertEquals("Amina Khan", amina.getName());
    assertEquals("ACTIVE", amina.getStatus());
  }

  @Test
  void duplicateCreateRejected() {
    CustomerService service = new CustomerService(new InMemoryCustomerRepository());
    assertThrows(IllegalStateException.class,
        () -> service.create(Customer.amina(), "lab-request-001"));
  }
}
