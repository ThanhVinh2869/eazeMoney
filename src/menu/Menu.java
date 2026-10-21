package menu;

import java.util.List;
import java.util.Scanner;

import login.Account;
import util.InputReader;

public abstract class Menu {
    protected final Scanner scanner;
    protected final InputReader reader;

    protected Menu(Scanner scanner) {
        this.scanner = scanner;
        this.reader = new InputReader(scanner);
    }

    protected abstract String getTitle();
    protected abstract List<String> getOptions();
    protected abstract void handleOption(int option);
//    protected abstract void onExit();

    protected boolean shouldExit() {
        return false;
    }

    protected String getExitLabel() {
        return "Back";
    }

    private void displayMenu() {
        System.out.println();
        System.out.println("==== " + getTitle() + " ====");
        List<String> options = getOptions();
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        System.out.println("0. " + getExitLabel());
    }

    public void runMenu() {
        boolean running = true;
        while (running && !shouldExit()) {
            displayMenu();

            int optionSize = getOptions().size();
            int choice = reader.readIntNumberBetween("Choose an option: ", 0, optionSize);

            if (choice == 0) {
                running = false;
            } else if (choice >= 1 && choice <= optionSize) {
                handleOption(choice);
            } else {
                System.out.println("Invalid choice, please try again.");
            }
        }

//        onExit();
    }
}
