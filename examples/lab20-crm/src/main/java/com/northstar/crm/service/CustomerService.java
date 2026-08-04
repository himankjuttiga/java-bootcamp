package com.northstar.crm.service;

import com.northstar.crm.model.Customer;
import com.northstar.crm.repository.CustomerRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public Customer create(Customer customer, String correlationId) {
        MDC.put("cust", customer.getCustomerId());
        MDC.put("op", "create");
        try {
            if (customer.getCustomerId() == null || customer.getCustomerId().isBlank()) {
                log.warn("Create rejected reason=missing_customer_id");
                throw new IllegalArgumentException("customerId required [" + correlationId + "]");
            }
            log.info("Creating customer");
            Customer saved = repository.save(customer);
            log.info("Customer created status={}", saved.getStatus());
            return saved;
        } finally {
            MDC.remove("cust");
            MDC.remove("op");
        }
    }

    public Optional<Customer> findById(String customerId) {
        MDC.put("cust", customerId);
        MDC.put("op", "get");
        try {
            log.info("Loading customer");
            Optional<Customer> result = repository.findById(customerId);
            if (result.isEmpty()) {
                log.warn("Customer not found");
            }
            return result;
        } finally {
            MDC.remove("cust");
            MDC.remove("op");
        }
    }
}
