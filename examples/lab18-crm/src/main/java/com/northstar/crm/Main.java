package com.northstar.crm;

import com.northstar.crm.api.ApiResult;
import com.northstar.crm.api.CustomerApiFacade;
import com.northstar.crm.dto.CustomerRequestDTO;
import com.northstar.crm.entity.Customer;
import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.repository.CustomerRepository;
import com.northstar.crm.repository.InMemoryCustomerRepository;
import com.northstar.crm.service.CustomerService;
import com.northstar.crm.service.CustomerValidator;
import com.northstar.crm.service.DefaultCustomerService;

import java.time.LocalDateTime;

/**
 * Lab 16 demo: every failure path prints the same ErrorResponse JSON shape,
 * always carrying correlationId lab-request-001, never a stack trace.
 */
public class Main {

    public static void main(String[] args) {
        String correlationId = "lab-request-001";

        CustomerRepository repo = new InMemoryCustomerRepository();
        CustomerValidator validator = new CustomerValidator(repo);
        CustomerService service = new DefaultCustomerService(repo, validator);
        CustomerApiFacade api = new CustomerApiFacade(service);

        // Fixtures
        service.addCustomer(new Customer("CUS-1001", "Amina Khan", "amina.khan@example.com",
                null, CustomerStatus.ACTIVE, LocalDateTime.now()));    // ACTIVE
        service.addCustomer(new Customer("CUS-1002", "Ravi Singh", "ravi.singh@example.com",
                null, CustomerStatus.PROSPECT, LocalDateTime.now()));  // PROSPECT

        // Happy path: activate Ravi (PROSPECT -> ACTIVE)
        print("200 activate Ravi", api.changeStatus("CUS-1002", CustomerStatus.ACTIVE, correlationId));

        // 400 validation: invalid email
        var badEmail = new CustomerRequestDTO("CUS-2001", "Test User", "not-an-email", "PROSPECT");
        print("400 validation", api.create(badEmail, correlationId));

        // 404 not found: CUS-9999
        print("404 not found", api.getById("CUS-9999", correlationId));

        // 409 conflict: illegal ACTIVE -> PROSPECT on Amina
        print("409 conflict", api.changeStatus("CUS-1001", CustomerStatus.PROSPECT, correlationId));

        // Invariant: Amina remains ACTIVE after the rejected transition
        System.out.println("CUS-1001 still: "
                + service.findById("CUS-1001").orElseThrow().getStatus());
    }

    private static void print(String label, ApiResult result) {
        if (result instanceof ApiResult.Ok ok) {
            System.out.println(label + " -> OK " + ok.body());
        } else if (result instanceof ApiResult.Fail fail) {
            System.out.println(label + " -> FAIL " + fail.error().toJson());
        }
    }
}
