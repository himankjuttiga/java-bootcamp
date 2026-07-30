package com.northstar.crm;

import com.northstar.crm.api.CustomerApiFacade;
import com.northstar.crm.dto.CustomerRequestDTO;
import com.northstar.crm.dto.CustomerResponseDTO;
import com.northstar.crm.service.CustomerService;

public class Main {
    public static void main(String[] args) {
        String correlationId = "lab-request-001";
        CustomerApiFacade api = new CustomerApiFacade(new CustomerService());

        // Create both customers via request DTOs; facade returns response DTOs only
        CustomerResponseDTO amina = api.create(
                new CustomerRequestDTO("CUS-1001", "Amina Khan", "amina.khan@example.com", "ACTIVE"),
                correlationId);
        CustomerResponseDTO ravi = api.create(
                new CustomerRequestDTO("CUS-1002", "Ravi Singh", "ravi.singh@example.com", "PROSPECT"),
                correlationId);
        System.out.println("Created: " + amina);
        System.out.println("Created: " + ravi);

        // Reads return DTOs, never the entity
        System.out.println("Get CUS-1001: " + api.get("CUS-1001", correlationId));
        System.out.println("Get CUS-1002: " + api.get("CUS-1002", correlationId));

        // Invalid email rejected before the service, correlation echoed
        try {
            api.create(new CustomerRequestDTO("CUS-1003", "Bad Email", "not-an-email", "ACTIVE"),
                    correlationId);
        } catch (IllegalArgumentException ex) {
            System.out.println("Invalid rejected: " + ex.getMessage());
        }

        // Unknown id rejected with correlation
        try {
            api.get("CUS-9999", correlationId);
        } catch (IllegalArgumentException ex) {
            System.out.println("Unknown rejected: " + ex.getMessage());
        }
    }
}