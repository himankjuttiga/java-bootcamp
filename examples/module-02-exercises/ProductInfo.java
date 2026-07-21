import java.util.Scanner;

public class ProductInfo {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Product name: ");
        String name = scanner.nextLine();   // text may include spaces

        System.out.print("Quantity: ");
        // Read a full line, then convert — avoids nextInt leftover-newline issues
        int qty = Integer.parseInt(scanner.nextLine());

        System.out.print("Price: ");
        double price = Double.parseDouble(scanner.nextLine());

        // %.2f = show two digits after the decimal (money-style)
        System.out.printf("Product: %s | Qty: %d | Price: %.2f%n", name, qty, price);

        scanner.close();
    }
}