package com.northstar.crm.repository;

import com.northstar.crm.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCustomerRepository implements CustomerRepository {
  private final Map<String, Customer> store = new ConcurrentHashMap<>();

  public InMemoryCustomerRepository() {
    store.put("CUS-1001", Customer.amina());
    store.put("CUS-1002", Customer.ravi());
  }

  @Override
  public Customer save(Customer customer) {
    store.put(customer.getId(), customer);
    return customer;
  }

  @Override
  public Optional<Customer> findById(String id) {
    return Optional.ofNullable(store.get(id));
  }

  @Override
  public List<Customer> findAll() {
    return List.copyOf(store.values());
  }

  @Override
  public boolean existsById(String id) {
    return store.containsKey(id);
  }
}
