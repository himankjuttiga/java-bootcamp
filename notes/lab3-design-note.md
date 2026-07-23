# Lab 3 Design Note — Banking Management System

Package: com.academy.bank

## Inheritance and polymorphism (in my own words)

Account is an abstract base class. It holds the fields and behaviour every
account shares: the account number, the balance, the owning customer, and the
deposit and withdraw logic. It cannot be instantiated on its own because a
plain "account" has no product type, so displayAccount() is declared abstract
and each subclass must supply its own version.

SavingsAccount and CurrentAccount both extend Account. Savings adds an interest
rate and overrides calculateInterest(). Current adds a transaction fee and
overrides calculateCharges(), which the base withdraw method folds into the
total deduction. Because both are Account subtypes, BankService stores them
together in a single Account[].

Polymorphism shows up in displayAccounts(). The loop calls
accounts[i].displayAccount() on an Account reference, but at runtime Java
dispatches to the actual object's override, so a savings account prints its
interest and a current account prints its fee, with no casting and no
instanceof checks.

## Interface

Printable is a one method contract, printDetails(). Customer, SavingsAccount,
and CurrentAccount all implement it. This is composition of behaviour by
contract rather than by inheritance: a customer is not an account, but both can
be printed, so both satisfy Printable.

## SOLID checklist

| Principle | Evidence in this project |
| --------- | ------------------------ |
| SRP | Each class has one job. Models hold data and their own behaviour. BankService owns orchestration and storage. Main owns only the menu loop. |
| OCP | Adding a new account type means writing a new subclass of Account. The display loop and storage array keep working without edits, because they depend on the abstract Account. |
| LSP | SavingsAccount and CurrentAccount are usable anywhere an Account is expected, including the shared Account[] and the polymorphic display loop. |
| ISP | Printable exposes a single method. Nothing is forced to implement behaviour it does not need. |
| DIP | Main depends on the BankService public methods, not on the raw arrays inside it. The service hides its storage details. |

## Encapsulation

Account keeps accountNumber, balance, and customer private. Balance changes
happen only through deposit and withdraw, and setBalance is protected so
subtypes and base operations can update it safely without exposing it to the
rest of the program.