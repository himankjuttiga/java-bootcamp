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
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
            if (id <= 0) { System.out.println("Invalid ID."); return; }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID."); return;
        }
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId() == id) {
                System.out.println("Duplicate ID. Student not added."); return;
            }
        }
        System.out.print("Name : ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) { System.out.println("Name cannot be empty."); return; }

        System.out.print("Course : ");
        String course = scanner.nextLine().trim();
        if (course.isEmpty()) { System.out.println("Course cannot be empty."); return; }

        System.out.print("Marks : ");
        double marks;
        try {
            marks = Double.parseDouble(scanner.nextLine().trim());
            if (marks < 0 || marks > 100) { System.out.println("Marks must be 0-100."); return; }
        } catch (NumberFormatException e) {
            System.out.println("Invalid marks."); return;
        }
        students[studentCount] = new Student(id, name, course, marks);
        studentCount++;
        System.out.println("Student Added Successfully.");
    }

    public void displayStudents() {
        if (studentCount == 0) { System.out.println("No students to display."); return; }
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
        if (studentCount == 0) { System.out.println("No students to search."); return; }
        System.out.print("Enter Student ID : ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID."); return;
        }
        for (int i = 0; i < studentCount; i++) {
            if (students[i].getStudentId() == id) {
                students[i].display(); return;
            }
        }
        System.out.println("Student Not Found.");
    }

    public void calculateAverage() {
        if (studentCount == 0) { System.out.println("No students available."); return; }
        double total = 0;
        for (int i = 0; i < studentCount; i++) {
            total += students[i].getMarks();
        }
        System.out.printf("Average Marks : %.2f%n", total / studentCount);
    }
}