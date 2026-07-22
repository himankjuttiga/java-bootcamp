package com.academy.student;

import java.util.Scanner;

public class StudentManager {

    private static final int MAX_STUDENTS = 20;

    private final Student[] students = new Student[MAX_STUDENTS];
    private int studentCount = 0;
    private final Scanner scanner;

    public StudentManager(Scanner scanner) {
        this.scanner = scanner;
    }

    public void displayMenu() {
        System.out.println("====================================");
        System.out.println("Student Management System");
        System.out.println("====================================");
        System.out.println("1. Add Student");
        System.out.println("2. Display Students");
        System.out.println("3. Search Student");
        System.out.println("4. Average Marks");
        System.out.println("5. Exit");
        System.out.print("Enter Choice : ");
    }

    public void addStudent() {
        if (studentCount >= MAX_STUDENTS) {
            System.out.println("Student list is full.");
            return;
        }

        System.out.print("Student ID : ");
        String idInput = scanner.nextLine().trim();

        // check every character is a digit
        boolean idValid = !idInput.isEmpty();
        for (int i = 0; i < idInput.length(); i++) {
            char c = idInput.charAt(i);
            if (c < '0' || c > '9') {
                idValid = false;
            }
        }
        if (!idValid) {
            System.out.println("Invalid ID.");
            return;
        }
        int id = Integer.parseInt(idInput);

        // check for duplicate ID
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId() == id) {
                System.out.println("Duplicate ID. Student not added.");
                return;
            }
        }

        System.out.print("Name : ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Course : ");
        String course = scanner.nextLine().trim();
        if (course.isEmpty()) {
            System.out.println("Course cannot be empty.");
            return;
        }

        System.out.print("Marks : ");
        String marksInput = scanner.nextLine().trim();

        // check digits with at most one decimal point
        boolean marksValid = !marksInput.isEmpty();
        int dotCount = 0;
        for (int i = 0; i < marksInput.length(); i++) {
            char c = marksInput.charAt(i);
            if (c == '.') {
                dotCount++;
                if (dotCount > 1) {
                    marksValid = false;
                }
            } else if (c < '0' || c > '9') {
                marksValid = false;
            }
        }
        if (!marksValid) {
            System.out.println("Invalid marks.");
            return;
        }
        double marks = Double.parseDouble(marksInput);
        if (marks < 0 || marks > 100) {
            System.out.println("Marks must be 0-100.");
            return;
        }

        students[studentCount] = new Student(id, name, course, marks);
        studentCount++;
        System.out.println("Student Added Successfully.");
    }

    public void displayStudents() {
        if (studentCount == 0) {
            System.out.println("No students to display.");
            return;
        }
        System.out.println("----------------------------------------------------------");
        System.out.printf("%-8s %-20s %-15s %-8s%n", "ID", "Name", "Course", "Marks");
        System.out.println("----------------------------------------------------------");
        for (int i = 0; i < studentCount; i++) {
            System.out.printf("%-8d %-20s %-15s %-8.2f%n",
                    students[i].getStudentId(),
                    students[i].getName(),
                    students[i].getCourse(),
                    students[i].getMarks());
        }
        System.out.println("----------------------------------------------------------");
    }

    public void searchStudent() {
        if (studentCount == 0) {
            System.out.println("No students to search.");
            return;
        }
        System.out.print("Enter Student ID : ");
        String idInput = scanner.nextLine().trim();

        boolean idValid = !idInput.isEmpty();
        for (int i = 0; i < idInput.length(); i++) {
            char c = idInput.charAt(i);
            if (c < '0' || c > '9') {
                idValid = false;
            }
        }
        if (!idValid) {
            System.out.println("Invalid ID.");
            return;
        }
        int id = Integer.parseInt(idInput);

        boolean found = false;
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId() == id) {
                students[i].display();
                found = true;
            }
        }
        if (!found) {
            System.out.println("Student Not Found.");
        }
    }

    public void calculateAverage() {
        if (studentCount == 0) {
            System.out.println("No students available.");
            return;
        }
        double total = 0;
        for (int i = 0; i < studentCount; i++) {
            total += students[i].getMarks();
        }
        System.out.printf("Average Marks : %.2f%n", total / studentCount);
    }
}