package com.northstar.crm.service;

import com.northstar.crm.dto.CustomerRequest;
import com.northstar.crm.model.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomerService {
  private final Map<String, Customer> store = new ConcurrentHashMap<>();

  /**
   * Lab 35: seeding is switchable so the SPA's empty state can be demonstrated without
   * editing code. Default stays true, so Lab 29 behaviour and its tests are unchanged.
   * Run empty with: mvn spring-boot:run -Dspring-boot.run.arguments=--northstar.crm.seed-fixtures=false
   */
  public CustomerService(@Value("${northstar.crm.seed-fixtures:true}") boolean seedFixtures) {
    if (seedFixtures) {
      store.put("CUS-1001", Customer.amina());
      store.put("CUS-1002", Customer.ravi());
    }
  }

  /** Lab 35: the SPA needs a stable list endpoint, ordered so the UI does not reshuffle. */
  public List<Customer> list() {
    return store.values().stream()
        .sorted(Comparator.comparing(Customer::getId))
        .toList();
  }

  /** Lab 35: full replace of an existing record; the path id always wins over the body id. */
  public Customer update(String id, CustomerRequest request, String correlationId) {
    if (!store.containsKey(id)) {
      throw new IllegalArgumentException("Customer not found: " + id);
    }
    Customer updated = new Customer(id, request.getName(), request.getEmail(), request.getStatus());
    store.put(id, updated);
    return updated;
  }

  public Customer create(CustomerRequest request, String correlationId) {
    if (store.containsKey(request.getId())) {
      throw new IllegalStateException("Duplicate customer: " + request.getId());
    }
    Customer c = new Customer(request.getId(), request.getName(), request.getEmail(), request.getStatus());
    store.put(c.getId(), c);
    return c;
  }

  public Customer get(String id) {
    Customer c = store.get(id);
    if (c == null) throw new IllegalArgumentException("Customer not found: " + id);
    return c;
  }
}
