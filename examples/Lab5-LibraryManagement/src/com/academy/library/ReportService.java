package com.academy.library;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
public class ReportService {
    private final LibraryService libraryService;
    public ReportService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }
    public void displaySummaryReport() {
        int totalBooks = libraryService.getBooks().size();
        int borrowedBooks = libraryService.getBorrowRecords().size();
        int availableBooks = totalBooks - borrowedBooks;
        int totalMembers = libraryService.getMembers().size();
        System.out.println("Reports");
        System.out.println("Books : " + totalBooks);
        System.out.println("Borrowed : " + borrowedBooks);
        System.out.println("Available : " + availableBooks);
        System.out.println("Members : " + totalMembers);
        System.out.println("Most Popular Category : " + findMostPopularCategory());
    }
    public Path exportReportToFile(String fileName) throws IOException {
        int totalBooks = libraryService.getBooks().size();
        int borrowedBooks = libraryService.getBorrowRecords().size();
        int availableBooks = totalBooks - borrowedBooks;
        int totalMembers = libraryService.getMembers().size();
        StringBuilder builder = new StringBuilder();
        builder.append("Reports\n");
        builder.append("Books : ").append(totalBooks).append("\n");
        builder.append("Borrowed : ").append(borrowedBooks).append("\n");
        builder.append("Available : ").append(availableBooks).append("\n");
        builder.append("Members : ").append(totalMembers).append("\n");
        builder.append("Most Popular Category : ").append(findMostPopularCategory()).append("\n");
        builder.append("\nBooks per Category\n");
        libraryService.getCategoryBookCount().forEach((category, count) ->
                builder.append(category).append(" : ").append(count).append("\n"));
        Path path = Path.of(fileName);
        Files.writeString(path, builder.toString());
        return path;
    }
    private String findMostPopularCategory() {
        return libraryService.getCategoryBookCount().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");
    }
}