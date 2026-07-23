package com.academy.bank;

import java.util.Scanner;

public class BankService {
    private static final int MAX_CUSTOMERS = 50;
    private static final int MAX_ACCOUNTS = 100;
    private static final int MAX_TRANSACTIONS = 500;

    private final Customer[] customers = new Customer[MAX_CUSTOMERS];
    private final Account[] accounts = new Account[MAX_ACCOUNTS];
    private final Transaction[] transactions = new Transaction[MAX_TRANSACTIONS];

    private int customerCount = 0;
    private int accountCount = 0;
    private int transactionCount = 0;
    private int nextAccountNumber = 10001;
    private int nextTransactionNumber = 1;

    private final Scanner scanner;

    public BankService(Scanner scanner) {
        this.scanner = scanner;
    }

    public void createCustomer() {
        scanner.nextLine();
        System.out.print("Customer ID : ");
        String id = scanner.nextLine();
        if (findCustomer(id) != null) {
            System.out.println("Customer ID already exists.");
            return;
        }
        System.out.print("Name : ");
        String name = scanner.nextLine();
        System.out.print("Email : ");
        String email = scanner.nextLine();
        System.out.print("Phone : ");
        String phone = scanner.nextLine();
        customers[customerCount++] = new Customer(id, name, email, phone);
        System.out.println("Customer Created Successfully.");
    }

    public void createSavingsAccount() {
        scanner.nextLine();
        System.out.print("Customer ID : ");
        Customer c = findCustomer(scanner.nextLine());
        if (c == null) {
            System.out.println("Customer not found.");
            return;
        }
        System.out.print("Initial Balance : ");
        double balance = scanner.nextDouble();
        System.out.print("Interest Rate (%) : ");
        double rate = scanner.nextDouble();
        String no = String.valueOf(nextAccountNumber++);
        accounts[accountCount++] = new SavingsAccount(no, balance, c, rate);
        System.out.println("Savings Account Created.");
        System.out.println("Account Number : " + no);
        System.out.println("Balance : " + balance);
        System.out.println("Interest Rate : " + rate + "%");
    }

    public void createCurrentAccount() {
        scanner.nextLine();
        System.out.print("Customer ID : ");
        Customer c = findCustomer(scanner.nextLine());
        if (c == null) {
            System.out.println("Customer not found.");
            return;
        }
        System.out.print("Initial Balance : ");
        double balance = scanner.nextDouble();
        System.out.print("Transaction Fee : ");
        double fee = scanner.nextDouble();
        String no = String.valueOf(nextAccountNumber++);
        accounts[accountCount++] = new CurrentAccount(no, balance, c, fee);
        System.out.println("Current Account Created.");
        System.out.println("Account Number : " + no);
        System.out.println("Balance : " + balance);
        System.out.println("Transaction Fee : " + fee);
    }

    public void deposit() {
        scanner.nextLine();
        System.out.print("Account Number : ");
        Account a = findAccount(scanner.nextLine());
        if (a == null) {
            System.out.println("Account not found.");
            return;
        }
        System.out.print("Deposit Amount : ");
        double amount = scanner.nextDouble();
        a.deposit(amount);
        recordTransaction(amount, "Deposit", a.getAccountNumber());
        System.out.println("Balance Updated : " + a.getBalance());
    }

    public void withdraw() {
        scanner.nextLine();
        System.out.print("Account Number : ");
        Account a = findAccount(scanner.nextLine());
        if (a == null) {
            System.out.println("Account not found.");
            return;
        }
        System.out.print("Withdraw : ");
        double amount = scanner.nextDouble();
        if (a.withdraw(amount)) {
            recordTransaction(amount, "Withdraw", a.getAccountNumber());
            System.out.println("Balance Updated : " + a.getBalance());
        } else {
            System.out.println("Insufficient Balance.");
        }
    }

    public void displayAccounts() {
        for (int i = 0; i < accountCount; i++) {
            accounts[i].displayAccount();
            System.out.println("----------------------------------");
        }
    }

    public void displayCustomers() {
        for (int i = 0; i < customerCount; i++) {
            customers[i].printDetails();
            System.out.println("----------------------------------");
        }
    }

    private Customer findCustomer(String id) {
        for (int i = 0; i < customerCount; i++)
            if (customers[i].getCustomerId().equals(id)) return customers[i];
        return null;
    }

    private Account findAccount(String no) {
        for (int i = 0; i < accountCount; i++)
            if (accounts[i].getAccountNumber().equals(no)) return accounts[i];
        return null;
    }

    private void recordTransaction(double amount, String type, String accNo) {
        transactions[transactionCount++] = new Transaction(
                String.valueOf(nextTransactionNumber++), accNo, type, amount, "today");
    }
}