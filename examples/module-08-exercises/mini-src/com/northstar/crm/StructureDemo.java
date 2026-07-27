package com.northstar.crm;

import com.northstar.crm.dto.CustomerRequest;
import com.northstar.crm.dto.CustomerResponse;
import com.northstar.crm.entity.Customer;

public class StructureDemo {
    public static void main(String[] args) {
        // TODO: create a CustomerRequest with name "Amina Khan" and email "amina@example.test"
        CustomerRequest request =
                new CustomerRequest("Amina Khan", "amina@example.com");

        // TODO: build a Customer entity — id "CUS-1001", name from request, status "ACTIVE"
        Customer entity =
                new Customer(
                        "CUS-1001",
                        request.getName(),
                        "ACTIVE");

        // TODO: map entity fields into a CustomerResponse
        CustomerResponse response =
                new CustomerResponse(
                        entity.getId(),
                        entity.getName(),
                        entity.getStatus());

        System.out.println(response.summary());
    }
}