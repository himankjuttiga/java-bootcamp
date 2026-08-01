package com.northstar.crm.service;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceBddMockTest {

    @Mock
    CustomerRepository repository;
    DefaultCustomerService service;

    @BeforeEach
    void setUp() {
        service = new DefaultCustomerService(repository, new CustomerValidator(repository));
    }

    @Test
    void givenProspectWhenActivateThenSavedActive() {
        Customer ravi = Customer.ravi(); // CUS-1002 PROSPECT
        given(repository.findById("CUS-1002")).willReturn(Optional.of(ravi));
        // changeStatus mutates and saves the same instance; match on it by id-based equals.
        given(repository.save(ravi)).willReturn(ravi);

        Customer updated = service.changeStatus("CUS-1002", CustomerStatus.ACTIVE, "lab-request-001");

        then(repository).should().findById("CUS-1002");
        then(repository).should().save(ravi);
        assertEquals(CustomerStatus.ACTIVE, updated.getStatus());
    }
}
