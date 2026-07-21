import java.util.Scanner;

public class LoopsDemo {
    public static void main(String[] args) {
        // for: use when you know the number of repetitions up front
        System.out.println("Multiplication table for 5:");
        for (int i = 1; i <= 5; i++) {
            System.out.println("5 x " + i + " = " + (5 * i));
        }

        // while: use when repetition depends on a condition, checked before each pass
        int count = 3;
        while (count > 0) {
            System.out.println("Countdown: " + count);
            count--;                        // must move toward false, or this loops forever
        }

        // do-while: body runs once before the condition is checked
        Scanner scanner = new Scanner(System.in);
        String choice;
        do {
            System.out.print("Type 'menu' to see it again, anything else to quit: ");
            choice = scanner.nextLine();
            if (choice.equals("menu")) {
                System.out.println("1) Add  2) Withdraw  3) Exit");
            }
        } while (choice.equals("menu"));

        scanner.close();
    }
}