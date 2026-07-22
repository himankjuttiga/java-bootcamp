public class InheritanceDemo {
    public static void main(String[] args) {
        // TODO: add FrozenAccount(100) — same loop, no special-casing
        Account[] accounts = {
                new SavingsAccount(100.00),
                new CurrentAccount(100.00),
                new FrozenAccount(100)
        };

        for (Account account : accounts) {
            // TODO: capture withdraw result; print type, ok flag, and balance
            boolean ok = account.withdraw(20.00);
            System.out.printf("%s withdraw=%s balance=%.2f%n",
                    account.getAccountType(), ok, account.getBalance());
        }
    }
}