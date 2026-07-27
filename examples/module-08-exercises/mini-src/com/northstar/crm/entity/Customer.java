package com.northstar.crm.entity;

public class Customer {
    // TODO: declare three final String fields — id, name, status
    private final String id;
    private final String name;
    private final String status;

    public Customer(String id, String name, String status) {
        // TODO: assign each parameter to its matching field (this._____)
        this.id = id;
        this.name = name;
        this.status = status;
    }

    // TODO: add three getters — getId(), getName(), getStatus()
    public String getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
}