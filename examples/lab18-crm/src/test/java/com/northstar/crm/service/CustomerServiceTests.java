package com.northstar.crm.service;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.exception.BusinessException;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CustomerServiceTests {

    DefaultCustomerService service;

    @BeforeEach
    void setUp() {
        InMemoryCustomerRepository repo = new InMemoryCustomerRepository();
        service = new DefaultCustomerService(repo, new CustomerValidator(repo));
    }

    private Customer amina() {
        return new Customer("CUS-1001", "Amina Khan", "amina.khan@example.com",
                null, CustomerStatus.ACTIVE, LocalDateTime.now());
    }

    private Customer ravi() {
        return new Customer("CUS-1002", "Ravi Singh", "ravi.singh@example.com",
                null, CustomerStatus.PROSPECT, LocalDateTime.now());
    }

    @Test
    void addAndActivateRaviHappyPath() {
        service.addCustomer(amina());
        service.addCustomer(ravi());

        Customer activated = service.changeStatus("CUS-1002", CustomerStatus.ACTIVE, "lab-request-001");

        assertEquals("CUS-1002", activated.getCustomerId());
        assertEquals(CustomerStatus.ACTIVE, activated.getStatus());
        assertEquals(CustomerStatus.ACTIVE,
                service.findById("CUS-1002").orElseThrow().getStatus());
    }

    @Test
    void duplicateIdThrowsConflict() {
        service.addCustomer(amina());
        assertThrows(BusinessException.class, () -> service.addCustomer(amina()));
    }

    @Test
    void illegalTransitionThrowsConflict() {
        service.addCustomer(amina());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.changeStatus("CUS-1001", CustomerStatus.PROSPECT, "lab-request-001"));
        assertEquals(409, ex.getStatusHint());
        assertEquals("BUSINESS_CONFLICT", ex.getCode());
        assertEquals(CustomerStatus.ACTIVE,
                service.findById("CUS-1001").orElseThrow().getStatus());
    }

    @Test
    void missingCustomerThrowsNotFound() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.changeStatus("CUS-9999", CustomerStatus.ACTIVE, "lab-request-001"));
        assertEquals(404, ex.getStatusHint());
        assertEquals("CUSTOMER_NOT_FOUND", ex.getCode());
        assertEquals("lab-request-001", ex.getCorrelationId());
    }
}
