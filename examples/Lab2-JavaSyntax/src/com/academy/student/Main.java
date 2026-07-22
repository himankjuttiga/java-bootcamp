package com.academy.student;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StudentManager studentManager = new StudentManager(scanner);

        while (true) {
            studentManager.displayMenu();

            String choiceInput = scanner.nextLine().trim();

            // check every character is a digit
            boolean choiceValid = !choiceInput.isEmpty();
            for (int i = 0; i < choiceInput.length(); i++) {
                char c = choiceInput.charAt(i);
                if (c < '0' || c > '9') {
                    choiceValid = false;
                }
            }
            if (!choiceValid) {
                System.out.println("Invalid Input");
                System.out.println("Please Try Again.");
                continue;
            }

            int choice = Integer.parseInt(choiceInput);

            switch (choice) {
                case 1 -> studentManager.addStudent();
                case 2 -> studentManager.displayStudents();
                case 3 -> studentManager.searchStudent();
                case 4 -> studentManager.calculateAverage();
                case 5 -> {
                    System.out.println("Thank You");
                    scanner.close();
                    return;
                }
                default -> {
                    System.out.println("Invalid Input");
                    System.out.println("Please Try Again.");
                }
            }

            System.out.println();
        }
    }
}