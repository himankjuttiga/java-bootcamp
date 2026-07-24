import java.util.ArrayList;
import java.util.List;

public class ArrayListDemo {
    public static void main(String[] args) {
        // TODO: declare List<String> books as new ArrayList<>()
        List<String> books = new ArrayList<>();

        // TODO: add four titles — include "Java Fundamentals" twice (duplicate allowed)
        //   "Java Fundamentals", "Clean Code", "Effective Java", "Java Fundamentals"
        books.add("Java Fundamentals");
        books.add("Clean Code");
        books.add("Effective Java");
        books.add("Java Fundamentals");

        books.set(1, "Clean Architecture");

        // TODO: search — does books contain "Effective Java"?
        boolean found = books.contains("Effective Java");

        // TODO: remove the first "Java Fundamentals" only (not removeIf)
        books.remove("Java Fundamentals");

        System.out.println(
                "Found Effective Java: " + found);
        System.out.println("Size: " + books.size());

        // TODO: print index and title for each element
        for (int i = 0; i < books.size(); i++) {
            System.out.printf(
                    "%d: %s%n", i, books.get(i));
        }
    }
}