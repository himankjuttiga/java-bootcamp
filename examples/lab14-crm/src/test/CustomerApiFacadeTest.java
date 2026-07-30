package com.northstar.crm.api;

import com.northstar.crm.dto.CustomerRequestDTO;
import com.northstar.crm.service.CustomerService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerApiFacadeTest {

    @Test
    void invalidEmailThrowsWithCorrelationId() {
        CustomerApiFacade api = new CustomerApiFacade(new CustomerService());
        CustomerRequestDTO bad =
                new CustomerRequestDTO("CUS-1001", "Amina Khan", "not-an-email", "ACTIVE");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> api.create(bad, "lab-request-001"));
        assertTrue(ex.getMessage().contains("lab-request-001"));
    }
}