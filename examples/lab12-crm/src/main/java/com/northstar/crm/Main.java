package com.northstar.crm;

import com.northstar.crm.entity.CustomerStatus;
import com.northstar.crm.service.CustomerService;

public class Main {
    public static void main(String[] args) {
        CustomerService service = new CustomerService(); // correlationId defaults to lab-request-001

        service.createCustomer("CUS-1001", "Amina Khan", "amina.khan@example.com",
                "555-0101", CustomerStatus.ACTIVE);
        service.createCustomer("CUS-1002", "Ravi Singh", "ravi.singh@example.com",
                "555-0102", CustomerStatus.PROSPECT);

        System.out.println("Get CUS-1001: " + service.getCustomer("CUS-1001").getFullName());

        service.updateStatus("CUS-1002", CustomerStatus.ACTIVE);
        System.out.println("After activation CUS-1002: " + service.getCustomer("CUS-1002").getStatus());

        // duplicate path
        try {
            service.createCustomer("CUS-1001", "Dup", "dup@example.com", null, CustomerStatus.ACTIVE);
        } catch (IllegalStateException ex) {
            System.out.println("Duplicate rejected: " + ex.getMessage());
        }

        // unknown path
        try {
            service.getCustomer("CUS-9999");
        } catch (IllegalArgumentException ex) {
            System.out.println("Unknown rejected: " + ex.getMessage());
        }
    }
}