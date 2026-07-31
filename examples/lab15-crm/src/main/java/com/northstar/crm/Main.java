package com.northstar.crm;

import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.repository.CustomerRepository;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import com.northstar.crm.service.CustomerService;
import com.northstar.crm.service.CustomerValidator;
import com.northstar.crm.service.DefaultCustomerService;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        String correlationId = "lab-request-001";

        // One shared repository for validator + service
        CustomerRepository repo = new InMemoryCustomerRepository();
        CustomerValidator validator = new CustomerValidator(repo);
        CustomerService service = new DefaultCustomerService(repo, validator);

        service.addCustomer(new Customer("CUS-1001", "Amina Khan", "amina.khan@example.com",
                null, CustomerStatus.ACTIVE, LocalDateTime.now()));   // ACTIVE
        service.addCustomer(new Customer("CUS-1002", "Ravi Singh", "ravi.singh@example.com",
                null, CustomerStatus.PROSPECT, LocalDateTime.now()));  // PROSPECT

        Customer activated = service.changeStatus("CUS-1002", CustomerStatus.ACTIVE, correlationId);
        System.out.printf("activated %s status=%s%n", activated.getCustomerId(), activated.getStatus());

        // Illegal ACTIVE -> PROSPECT on Amina; must fail and leave her ACTIVE
        try {
            service.changeStatus("CUS-1001", CustomerStatus.PROSPECT, correlationId);
        } catch (IllegalStateException ex) {
            System.out.println("expected failure: " + ex.getMessage());
        }
        System.out.println("CUS-1001 still: "
                + service.findById("CUS-1001").orElseThrow().getStatus());
    }
}