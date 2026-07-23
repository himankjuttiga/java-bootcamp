package com.academy.bank;

public class CurrentAccount extends Account implements Printable {
    private double transactionFee;

    public CurrentAccount(String accountNumber, double balance, Customer customer, double transactionFee) {
        super(accountNumber, balance, customer);
        this.transactionFee = transactionFee;
    }

    @Override
    public double calculateCharges() {
        return transactionFee;
    }

    @Override
    public String getAccountType() {
        return "Current";
    }

    @Override
    public void displayAccount() {
        System.out.println("Current Account");
        System.out.println("Account Number : " + getAccountNumber());
        System.out.println("Customer : " + getCustomer().getName());
        System.out.println("Balance : " + getBalance());
        System.out.println("Transaction Fee : " + transactionFee);
    }

    @Override
    public void printDetails() {
        displayAccount();
    }
}
