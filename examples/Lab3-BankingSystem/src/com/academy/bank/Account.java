package com.academy.bank;

public abstract class Account {
    private String accountNumber;
    private double balance;
    private Customer customer;

    protected Account(String accountNumber, double balance, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.customer = customer;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public Customer getCustomer() { return customer; }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit rejected: amount must be positive.");
            return;
        }
        setBalance(getBalance() + amount);
    }

    public boolean withdraw(double amount) {
        double total = amount + calculateCharges();
        if (amount <= 0 || total > getBalance()) {
            System.out.println("Insufficient funds or invalid amount.");
            return false;
        }
        setBalance(getBalance() - total);
        return true;
    }

    public double calculateCharges() {
        return 0.0;
    }

    public double calculateInterest() {
        return 0.0;
    }

    public String getAccountType() {
        return "Account";
    }

    public abstract void displayAccount();
}