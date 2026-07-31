package com.northstar.crm.service;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerValidatorTest {

    private CustomerValidator newValidator() {
        return new CustomerValidator(new InMemoryCustomerRepository());
    }

    @Test
    void allowsProspectToActive() {
        assertDoesNotThrow(() -> newValidator()
                .validateTransition(CustomerStatus.PROSPECT, CustomerStatus.ACTIVE, "lab-request-001"));
    }

    @Test
    void rejectsActiveToProspect() {
        assertThrows(IllegalStateException.class, () -> newValidator()
                .validateTransition(CustomerStatus.ACTIVE, CustomerStatus.PROSPECT, "lab-request-001"));
    }

    @Test
    void rejectsClosedToActive() {
        assertThrows(IllegalStateException.class, () -> newValidator()
                .validateTransition(CustomerStatus.CLOSED, CustomerStatus.ACTIVE, "lab-request-001"));
    }

    @Test
    void allowsActiveToSuspended() {
        assertDoesNotThrow(() -> newValidator()
                .validateTransition(CustomerStatus.ACTIVE, CustomerStatus.SUSPENDED, "lab-request-001"));
    }

    @Test
    void transitionMessageIncludesCorrelationId() {
        var ex = assertThrows(IllegalStateException.class, () -> newValidator()
                .validateTransition(CustomerStatus.ACTIVE, CustomerStatus.PROSPECT, "lab-request-001"));
        assertTrue(ex.getMessage().contains("lab-request-001"));
    }

    @Test
    void validateNewRejectsDuplicateId() {
        var repo = new InMemoryCustomerRepository();
        repo.save(new Customer("CUS-1001", "Amina Khan", "amina.khan@example.com", null,
                CustomerStatus.ACTIVE, LocalDateTime.now()));
        var validator = new CustomerValidator(repo);
        Customer dup = new Customer("CUS-1001", "Other", "other@example.com", null,
                CustomerStatus.PROSPECT, LocalDateTime.now());
        assertThrows(IllegalStateException.class, () -> validator.validateNew(dup));
    }

    @Test
    void validateNewRejectsDuplicateEmail() {
        var repo = new InMemoryCustomerRepository();
        repo.save(new Customer("CUS-1001", "Amina Khan", "amina.khan@example.com", null,
                CustomerStatus.ACTIVE, LocalDateTime.now()));
        var validator = new CustomerValidator(repo);
        Customer dup = new Customer("CUS-9999", "Other", "amina.khan@example.com", null,
                CustomerStatus.PROSPECT, LocalDateTime.now());
        assertThrows(IllegalStateException.class, () -> validator.validateNew(dup));
    }
}