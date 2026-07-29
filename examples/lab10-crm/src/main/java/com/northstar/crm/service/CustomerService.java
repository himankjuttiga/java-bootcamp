package com.northstar.crm.service;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.dto.CustomerRequest;
import com.northstar.crm.dto.CustomerResponse;

import java.util.*;

public class CustomerService {
    private final List<Customer> customers = new ArrayList<>();

    public Customer addCustomer(Customer customer) {
        if (customer == null || customer.getCustomerId() == null || customer.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be blank");
        }
        if (customers.stream().anyMatch(c -> c.getCustomerId().equals(customer.getCustomerId()))) {
            throw new IllegalStateException("Customer with ID " + customer.getCustomerId() + " already exists");
        }
        customers.add(customer);
        return customer;
    }

    public Optional<Customer> findByCustomerId(String customerId) {
        return customers.stream()
                .filter(c -> c.getCustomerId().equals(customerId))
                .findFirst();
    }

    public List<Customer> findByStatus(CustomerStatus status) {
        return customers.stream()
                .filter(c -> c.getStatus() == status)
                .toList();
    }

    public Customer updateStatus(String customerId, CustomerStatus newStatus) {
        Customer customer = findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + customerId + " not found"));
        customer.setStatus(newStatus);
        return customer;
    }

    public List<Customer> listAll() {
        return Collections.unmodifiableList(new ArrayList<>(customers));
    }

    // Lab 8/9 controller stubs kept so CustomerController still compiles.
    public CustomerResponse create(CustomerRequest request) {
        throw new UnsupportedOperationException("Lab 8 stub — implement later");
    }

    public CustomerResponse getById(String customerId) {
        throw new UnsupportedOperationException("Lab 8 stub — implement later");
    }
}