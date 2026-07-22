public class SolidDemo {
    // Business calculation: returns data; does not print.
    static double calculateInterest(
            double balance, double ratePercent) {
        // TODO: return balance * ratePercent / 100.0 (no System.out here)
        return balance * ratePercent / 100.0;
    }

    // Presentation: formats a value; does not calculate it.
    static void printInterest(double interest) {
        // TODO: printf "Interest earned: %.2f%n"
        System.out.printf("Interest earned: %.2f%n", interest);
    }

    public static void main(String[] args) {
        // TODO: calculate interest for 10_000 at 5%, then print it
        double interest = calculateInterest(10000, 5);
        printInterest(interest);
    }
}