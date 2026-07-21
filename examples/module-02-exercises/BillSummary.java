import java.util.Scanner;

public class BillSummary {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Product name: ");
        String name = scanner.nextLine();

        System.out.print("Quantity: ");
        int qty = Integer.parseInt(scanner.nextLine());

        System.out.print("Unit price: ");
        double price = Double.parseDouble(scanner.nextLine());

        double total = qty * price;           // before discount
        double discount = total * 0.10;       // 10% off
        double finalAmount = total - discount;

        System.out.println("--- Bill Summary ---");
        System.out.printf("Product: %s%n", name);
        System.out.printf("Quantity: %d%n", qty);
        System.out.printf("Unit price: %.2f%n", price);
        System.out.printf("Total: %.2f%n", total);
        // %% prints a literal % character inside printf
        System.out.printf("Discount (10%%): %.2f%n", discount);
        System.out.printf("Final amount: %.2f%n", finalAmount);

        scanner.close();
    }
}