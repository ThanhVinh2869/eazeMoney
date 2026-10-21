package util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputReader {
    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public int readInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                showError("Invalid int number, try again.");
            }
        }
    }

    public static boolean isNumberBetween(short number, short from, short to) {
        return (number >= from && number <= to);
    }

    public static boolean isNumberBetween(int number , int from, int to) {
        return (number >= from && number <= to);
    }

    public static boolean isNumberBetween(float number, float from, float to) {
        return (number >= from && number <= to);
    }

    public static boolean isNumberBetween(double number, double from, double to) {
        return (number >= from && number <= to);
    }

    public int readIntNumberBetween(String prompt, int from, int to) {
        int number = readInt(prompt);

        while (!isNumberBetween(number, from, to)) {
            System.out.println("Invalid choice, please try again.");
            number = readInt(prompt);
        }
        return number;
    }

    public String readString(String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine().trim();
    }

    public LocalDate readDate(String prompt) {
        System.out.print(prompt);
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

    public BigDecimal readBigDecimal() {
        while (true) {
            String value = readString("Amount: ");
            if (value == null) {
                return null;
            }
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid amount.");
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
            String answer = readString(question + " (y/n): ").toLowerCase();

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
