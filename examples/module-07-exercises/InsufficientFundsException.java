public class InsufficientFundsException
        extends Exception { // TODO: extend Exception (checked)
    private final double balance;
    private final double requested;

    public InsufficientFundsException(
            double balance, double requested) {
        // TODO: call super with formatted message:
        //   "Insufficient funds: balance=%.2f, requested=%.2f"
        super(String.format(
                "Insufficient funds: balance=%.2f, requested=%.2f",
                balance, requested));
        this.balance = balance;
        this.requested = requested;
    }

    public double getBalance() {
        return balance;
    }

    public double getRequested() {
        return requested;
    }
}