package com.northstar.crm.service;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.exception.BusinessException;
import com.northstar.crm.repository.CustomerRepository;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DefaultCustomerServiceTest {

    private CustomerService newService() {
        CustomerRepository repo = new InMemoryCustomerRepository();
        return new DefaultCustomerService(repo, new CustomerValidator(repo));
    }

    @Test
    void activatesRaviProspectToActive() {
        CustomerService service = newService();
        service.addCustomer(new Customer("CUS-1002", "Ravi Singh", "ravi.singh@example.com", null,
                CustomerStatus.PROSPECT, LocalDateTime.now()));   // PROSPECT
        Customer activated = service.changeStatus("CUS-1002", CustomerStatus.ACTIVE, "lab-request-001");
        assertEquals(CustomerStatus.ACTIVE, activated.getStatus());
    }

    @Test
    void illegalTransitionLeavesStatusUnchanged() {
        CustomerService service = newService();
        service.addCustomer(new Customer("CUS-1001", "Amina Khan", "amina.khan@example.com", null,
                CustomerStatus.ACTIVE, LocalDateTime.now()));      // ACTIVE
        assertThrows(BusinessException.class, () ->
                service.changeStatus("CUS-1001", CustomerStatus.PROSPECT, "lab-request-001"));
        assertEquals(CustomerStatus.ACTIVE,
                service.findById("CUS-1001").orElseThrow().getStatus());
    }

    @Test
    void notFoundOnChangeStatusMapsToBusinessException() {
        CustomerService service = newService();
        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.changeStatus("CUS-9999", CustomerStatus.ACTIVE, "lab-request-001"));
        assertEquals(404, ex.getStatusHint());
        assertEquals("lab-request-001", ex.getCorrelationId());
    }
}
