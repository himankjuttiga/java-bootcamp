# Banking domain notes

| Entity | Identity | Important attributes | Main responsibility |
| ------ | -------- | -------------------- | ------------------- |
| Customer | customerId | name, email, phone | Maintain customer profile |
| Account | accountNumber | owner, balance, accountType | Protect balance and perform deposits/withdrawals |
| Transaction | transactionId | account, type, amount, timestamp | Record one account operation |

## Relationships
- One Customer can own zero or more Accounts.
- One Account belongs to exactly one Customer.
- One Account can have many Transactions.
- One Transaction belongs to exactly one Account.

## Rules
- An account balance cannot be changed directly from outside Account.
- A deposit amount must be positive.
- A withdrawal cannot exceed the allowed balance.

## Design Decision

**Why should Account, rather than Main, decide whether a withdrawal is valid?**

Account owns the balance and is the only class that should be allowed to change it. If Main validated withdrawals directly, that business rule would live outside the object responsible for the data, making it easy for another part of the program to bypass the check and corrupt the balance. Keeping validation inside Account means the rule is enforced no matter where the withdrawal request comes from, and Main can stay a thin coordinator that just relays user input.


## Exercise 3 Notes
## Step 5 - Polymorphic Call Explanation

For the second loop iteration:
- Reference type: Account
- Object type: CurrentAccount
- Method chosen: CurrentAccount.withdraw

Even though the array is typed as Account[], each element still
remembers its real object type at runtime. When account.withdraw(20.00)
is called, Java looks at the actual object (CurrentAccount), not the
reference type (Account), and runs CurrentAccount's overridden withdraw
method instead of Account's. This is runtime polymorphism - the method
that executes depends on the object's real type, decided while the
program is running, not at compile time.

## Exercise 6 Notes 
## SRP spot-check
The original method could change because the formula changes or because
the output format changes. These are separate responsibilities.

Main should manage menu input, BankService should coordinate banking
operations, and domain classes should protect their own state.

## Exercise 7 Notes
## SOLID Spot-Check

**O — Open/Closed:** Adding FrozenAccount required creating a new class
only — SavingsAccount, CurrentAccount, and Account itself needed no changes.

**L — Liskov Substitution:** FrozenAccount can be used anywhere an Account
is expected because it still returns a boolean and leaves balance unchanged
on failure, matching what the loop already expects, instead of throwing an
unexpected exception.

**I — Interface Segregation:** If Printable also required sendEmailReceipt(),
every class implementing Printable (like SavingsAccount) would be forced to
implement an email method it has no use for, violating the idea that classes
shouldn't be forced to depend on methods they don't need.

**D — Dependency Inversion:** Declaring Account account = new FrozenAccount(100.00)
keeps the code depending on the Account abstraction rather than the concrete
FrozenAccount type, so the concrete class can be swapped later without
changing any code that uses the account variable.

## Five SOLID Letters
Single Responsibility, Open/Closed, Liskov Substitution, Interface
Segregation, Dependency Inversion.