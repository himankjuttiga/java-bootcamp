public class SavingsAccount extends Account {
    public SavingsAccount(double initialBalance) {
        // TODO: call parent constructor with initialBalance
        super(initialBalance);;
    }

    @Override
    public String getAccountType() {
        // TODO: return "Savings"
        return "Savings";
    }
}