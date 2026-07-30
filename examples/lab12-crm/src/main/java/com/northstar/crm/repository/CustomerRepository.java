package com.northstar.crm.repository;
import com.northstar.crm.entity.Customer;
import java.util.Optional;
/**
 * Persistence boundary. Lab 8: stub only.
 * Later: in-memory List, then JPA/PostgreSQL.
 *
 * Imports only entity (+ JDK) — no controller or dto (Lab 8 rule).
 */
public class CustomerRepository {
    public Optional<Customer> findById(String customerId) {
        throw new UnsupportedOperationException("Lab 8 stub — implement later");
    }
    public Customer save(Customer customer) {
        throw new UnsupportedOperationException("Lab 8 stub — implement later");
    }
}