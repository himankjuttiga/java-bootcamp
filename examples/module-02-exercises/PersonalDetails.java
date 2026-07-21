import java.util.Scanner;   // Scanner lives in java.util — must import it

public class PersonalDetails {
    public static void main(String[] args) {
        // One Scanner reading from the keyboard (System.in)
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your name: ");   // print = no newline after prompt
        String name = scanner.nextLine();        // read whole line as text

        System.out.print("Enter your age: ");
        int age = scanner.nextInt();             // read an int; leaves '\n' in the buffer
        scanner.nextLine();                      // consume that leftover newline (important!)

        System.out.print("Enter your city: ");
        String city = scanner.nextLine();        // now reads the city, not an empty line

        // %s = string, %d = int, %n = platform newline
        System.out.printf("Hello, %s! You are %d years old and live in %s.%n",
                name, age, city);

        scanner.close();                         // good habit when you own the Scanner
    }
}