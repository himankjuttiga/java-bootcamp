import java.util.Scanner;
public class Calculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("First number: ");
        double a = Double.parseDouble(scanner.nextLine());

        System.out.print("Second number: ");
        double b = Double.parseDouble(scanner.nextLine());

        System.out.printf("Sum: %.2f%n", a + b);
        System.out.printf("Difference: %.2f%n", a - b);
        System.out.printf("Product: %.2f%n", a * b);

        // double is preferred for division because int division truncates decimals.
        // e.g. 12 / 4 with int gives 3, but 12 / 5 with int gives 2 instead of 2.40.
        // double preserves the decimal, giving accurate results like 2.40.
        System.out.printf("Quotient: %.2f%n", a / b);

        scanner.close();
    }
}