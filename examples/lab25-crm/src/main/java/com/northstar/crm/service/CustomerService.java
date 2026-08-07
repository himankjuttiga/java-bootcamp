package com.northstar.crm.service;

import com.northstar.crm.model.Customer;
import com.northstar.crm.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
  private final CustomerRepository customerRepository;

  public CustomerService(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }

  public Customer create(Customer customer, String correlationId) {
    if (customerRepository.existsById(customer.getId())) {
      throw new IllegalStateException("Duplicate customer");
    }
    return customerRepository.save(customer);
  }

  public Customer get(String id) {
    return customerRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
  }

  public List<Customer> list() {
    return customerRepository.findAll();
  }
}
