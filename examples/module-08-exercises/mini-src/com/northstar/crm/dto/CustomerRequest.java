package com.northstar.crm.dto;

public class CustomerRequest {
    // TODO: boundary input fields only — name and email (no id, no status)
    private final String name;
    private final String email;

    public CustomerRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
}