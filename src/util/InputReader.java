package util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                showError("Invalid int number, try again.");
            }
        }
    }

    public int readChoice() {
        // TODO
        return -1;
    }

    public String readString() {
        return scanner.nextLine();
    }

    public LocalDate readDate() {
        while (true) {
            try {
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                showError("Invalid date. Use the format yyyy-MM-dd, e.g. 2026-09-15.");
            }
        }
    }

    public double readDouble() {
        while (true) {
            String line = scanner.nextLine().trim();
            try {
                return Double.parseDouble(line);
            } catch (NumberFormatException e) {
                showError("Invalid double number, try again.");
            }
        }
    }

    public double readPositiveDouble() {
        while (true) {
            double value = readDouble();
            if (value > 0) {
                return value;
            }
            showError("The amount must be greater than zero.");
        }
    }

    public boolean confirm(String question) {
        while (true) {
            System.out.print(question + " (y/n): ");
            String answer = readString().toLowerCase();

            if (answer.equals("y") || answer.equals("yes")) {
                return true;
            }
            if (answer.equals("n") || answer.equals("no")) {
                return false;
            }

            showError("Please answer y or n.");
        }
    }

    private void showError(String message) {
        System.out.println("[!] " + message);
    }
}
